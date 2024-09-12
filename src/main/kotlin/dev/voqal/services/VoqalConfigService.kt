package dev.voqal.services

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.google.common.io.Resources
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.ThreadingAssertions
import com.jetbrains.rd.util.Callable
import dev.voqal.config.ConfigurableSettings
import dev.voqal.config.VoqalConfig
import dev.voqal.config.settings.*
import dev.voqal.config.settings.LanguageModelSettings.LMProvider
import dev.voqal.config.settings.LanguageModelSettings.OProvider
import dev.voqal.provider.AiProvider
import dev.voqal.provider.clients.AiProvidersClient
import dev.voqal.provider.clients.anthropic.AnthropicClient
import dev.voqal.provider.clients.assemblyai.AssemblyAiClient
import dev.voqal.provider.clients.deepgram.DeepgramClient
import dev.voqal.provider.clients.deepseek.DeepSeekClient
import dev.voqal.provider.clients.fireworks.FireworksClient
import dev.voqal.provider.clients.googleapi.GoogleApiClient
import dev.voqal.provider.clients.groq.GroqClient
import dev.voqal.provider.clients.groq.GroqWhisperClient
import dev.voqal.provider.clients.helicone.HeliconeClient
import dev.voqal.provider.clients.huggingface.HuggingFaceClient
import dev.voqal.provider.clients.mistralai.MistralAiClient
import dev.voqal.provider.clients.ollama.OllamaClient
import dev.voqal.provider.clients.openai.OpenAiClient
import dev.voqal.provider.clients.picovoice.PicovoiceCobraClient
import dev.voqal.provider.clients.picovoice.PicovoiceLeopardClient
import dev.voqal.provider.clients.picovoice.PicovoiceOrcaClient
import dev.voqal.provider.clients.picovoice.error.PicovoiceError
import dev.voqal.provider.clients.togetherai.TogetherAiClient
import dev.voqal.provider.clients.vertexai.VertexAiClient
import dev.voqal.provider.clients.voqal.VoqalVadClient
import dev.voqal.provider.clients.whisper.WhisperClient
import dev.voqal.status.VoqalStatus
import io.ktor.client.engine.java.*
import io.vertx.core.json.JsonObject
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import java.net.URL

/**
 * Holds the project's current configuration.
 */
@Service(Service.Level.PROJECT)
class VoqalConfigService(private val project: Project) {

    private val log = project.getVoqalLogger(this::class)
    private val name = project.name
    private val login = project.name
    private var cachedConfig: VoqalConfig? = null
    private var aiProvider: AiProvider? = null
    private val configListeners = mutableListOf<(VoqalConfig) -> Unit>()
    private val syncLock = Any()

    fun onConfigChange(
        disposable: Disposable = project.service<ProjectScopedService>(),
        listener: (VoqalConfig) -> Unit
    ) {
        configListeners.add(listener)

        Disposer.register(disposable) {
            configListeners.remove(listener)
        }
    }

    private fun getEncryptedData(): String? {
        val serviceName = generateServiceName("Voqal", name)
        val attributes = CredentialAttributes(serviceName, login)
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            PasswordSafe.instance.getPassword(attributes)
        }).get()
    }

    private fun setEncryptedDate(voqalConfigJson: String?) {
        val serviceName = generateServiceName("Voqal", name)
        val attributes = CredentialAttributes(serviceName, login)
        ApplicationManager.getApplication().executeOnPooledThread {
            PasswordSafe.instance.set(attributes, Credentials(login, voqalConfigJson))
        }
    }

    fun updateConfig(settings: ConfigurableSettings): VoqalConfig {
        if (cachedConfig == null) {
            throw IllegalStateException("Config not initialized")
        }
        synchronized(syncLock) {
            val config = cachedConfig!!
            when (settings) {
                is VoqalConfig -> saveConfig(settings.copy())
                is SpeechToTextSettings -> saveConfig(config.copy(speechToTextSettings = settings))
                is TextToSpeechSettings -> saveConfig(config.copy(textToSpeechSettings = settings))
                is VoiceDetectionSettings -> saveConfig(config.copy(voiceDetectionSettings = settings))
                is LanguageModelsSettings -> saveConfig(config.copy(languageModelsSettings = settings))
                is PluginSettings -> saveConfig(config.copy(pluginSettings = settings))
                is PromptLibrarySettings -> saveConfig(config.copy(promptLibrarySettings = settings))
                is PromptSettings -> {
                    val promptSettings = config.promptLibrarySettings.prompts.toMutableList()
                    val index = promptSettings.indexOfFirst { it.promptName == settings.promptName }
                    if (index != -1) {
                        promptSettings[index] = settings
                        saveConfig(config.copy(promptLibrarySettings = config.promptLibrarySettings.copy(prompts = promptSettings)))
                    }
                }

                else -> throw IllegalArgumentException("Unknown setting type: ${settings::class.simpleName}")
            }
        }
        return cachedConfig!!
    }

    private fun saveConfig(config: VoqalConfig) {
        if (System.getProperty("VQL_TEST_MODE") != "true") {
            setEncryptedDate(config.toJson().toString())
        }
        cachedConfig = config.copy()
        configListeners.forEach { it.invoke(cachedConfig!!.copy()) }
        log.debug("Saved config: ${cachedConfig!!.withKeysRemoved()}")

        //save current config to disk if desired
        val configFile = File(project.basePath, ".voqal/config.json")
        if (configFile.exists()) {
            configFile.writeText(cachedConfig!!.toJson().encodePrettily())
            log.debug("Wrote config to file: ${cachedConfig!!.withKeysRemoved()}")
        }
    }

    fun getConfig(): VoqalConfig {
        if (cachedConfig != null) {
            return cachedConfig!!.copy()
        }

        synchronized(syncLock) {
            //check for .voqal/config.json before trying to load from password store
            val configFile = File(project.basePath, ".voqal/config.json")
            if (configFile.exists() && configFile.length() != 0L) {
                try {
                    cachedConfig = VoqalConfig(JsonObject(configFile.readText()))
                    log.debug("Loaded config from file: ${cachedConfig!!.withKeysRemoved()}")
                    return cachedConfig!!.copy()
                } catch (e: Exception) {
                    log.warn("Failed to load config from file. Reason: " + e.message)
                    saveConfig(VoqalConfig())
                }
            } else {
                try {
                    cachedConfig = VoqalConfig(JsonObject(getEncryptedData()))
                    log.debug("Loaded config: ${cachedConfig!!.withKeysRemoved()}")
                } catch (e: Exception) {
                    log.warn("Failed to load config. Reason: " + e.message)
                    saveConfig(VoqalConfig())
                }

                //save current config to disk if desired
                if (configFile.exists() && configFile.length() == 0L) {
                    configFile.writeText(cachedConfig!!.toJson().encodePrettily())
                    log.debug("Wrote config to file: ${cachedConfig!!.withKeysRemoved()}")
                }
            }
        }
        return cachedConfig!!.copy()
    }

    fun getAiProvider(): AiProvider {
        ThreadingAssertions.assertBackgroundThread()
        var aiProvider = this.aiProvider
        if (aiProvider == null) {
            synchronized(syncLock) {
                aiProvider = this.aiProvider
                if (aiProvider == null) {
                    aiProvider = initAiProvider()
                    this.aiProvider = aiProvider
                }
            }
        }
        return aiProvider!!
    }

    fun resetAiProvider() {
        synchronized(syncLock) {
            aiProvider?.let {
                log.debug("Disposing AI provider")
                Disposer.dispose(it)
                log.info("AI provider disposed")
            }
            aiProvider = null
        }
    }

    fun resetCachedConfig() {
        synchronized(syncLock) {
            cachedConfig = null
        }
    }

    private fun initAiProvider(): AiProvider {
        val voqalConfig = getConfig()
        log.debug("Initializing AI provider using config: ${voqalConfig.withKeysRemoved()}")
        val providersClient = AiProvidersClient(project).apply {
            Disposer.register(project.service<ProjectScopedService>(), this)
            setupVoiceActivityProvider(voqalConfig)
            setupSpeechToTextProvider(voqalConfig)
            setupLanguageModelProvider(voqalConfig)
            setupObservabilityProvider(voqalConfig)
            setupTextToSpeechProvider(voqalConfig)
        }
        if (providersClient.hasNecessaryProviders()) {
            log.info("AI provider successfully initialized")
        } else {
            log.warn("No AI provider initialized")
        }
        return providersClient
    }

    private fun AiProvidersClient.setupTextToSpeechProvider(voqalConfig: VoqalConfig) {
        when (voqalConfig.textToSpeechSettings.provider) {
            TextToSpeechSettings.TTSProvider.NONE -> {
                log.debug("No text-to-speech provider configured")
            }

            TextToSpeechSettings.TTSProvider.OPENAI -> {
                log.debug("Using OpenAI text-to-speech provider")
                if (voqalConfig.textToSpeechSettings.providerKey.isNotEmpty()) {
                    val openAiConfig = OpenAIConfig(
                        token = voqalConfig.textToSpeechSettings.providerKey,
                        organization = voqalConfig.textToSpeechSettings.orgId.takeIf { it.isNotEmpty() },
                        logging = LoggingConfig(LogLevel.None),
                        engine = JavaHttpEngine(JavaHttpConfig())
                    )
                    val openAI = OpenAiClient("", project, openAiConfig)
                    addTtsProvider(openAI)
                } else {
                    log.warnChat("Missing text-to-speech provider key")
                }
            }

            TextToSpeechSettings.TTSProvider.DEEPGRAM -> {
                log.debug("Using Deepgram text-to-speech provider")
                if (voqalConfig.speechToTextSettings.provider == SpeechToTextSettings.STTProvider.DEEPGRAM) {
                    log.debug("Reusing Deepgram client for speech-to-text")
                    val deepgramClient = asSttProvider() as DeepgramClient
                    addTtsProvider(deepgramClient)
                } else if (voqalConfig.textToSpeechSettings.providerKey.isNotEmpty()) {
                    val deepgramClient = DeepgramClient(
                        project = project,
                        providerKey = voqalConfig.textToSpeechSettings.providerKey
                    )
                    addTtsProvider(deepgramClient)
                } else {
                    log.warnChat("Missing text-to-speech provider key")
                }
            }

            TextToSpeechSettings.TTSProvider.PICOVOICE -> {
                log.debug("Using Picovoice text-to-speech provider")
                if (voqalConfig.textToSpeechSettings.providerKey.isNotEmpty()) {
                    try {
                        val orcaClient = PicovoiceOrcaClient(
                            project,
                            voqalConfig.textToSpeechSettings.providerKey
                        )
                        addTtsProvider(orcaClient)
                    } catch (e: PicovoiceError) {
                        log.warnChat("Unable to validate text-to-speech provider key")
                    }
                } else {
                    log.warnChat("Missing text-to-speech provider key")
                }
            }
        }
    }

    //todo: observability isn't per provider
    private fun AiProvidersClient.setupObservabilityProvider(voqalConfig: VoqalConfig) {
        val anyObservedModelSettings = voqalConfig.languageModelsSettings.models.firstOrNull {
            it.observabilityProvider != OProvider.None
        } ?: return
        when (anyObservedModelSettings.observabilityProvider) {
            OProvider.Helicone -> {
                log.debug("Using Helicone observability provider with OpenAI language model")
                if (anyObservedModelSettings.observabilityKey.isNotEmpty()) {
                    val heliconeClient = HeliconeClient(
                        project,
                        anyObservedModelSettings.observabilityKey,
                        anyObservedModelSettings.observabilityUserId
                    )
                    addObservabilityProvider(heliconeClient)
                } else {
                    log.warnChat("Missing observability provider key")
                }
            }

            OProvider.None -> Unit //nop
        }
    }

    private fun AiProvidersClient.setupLanguageModelProvider(voqalConfig: VoqalConfig) {
        voqalConfig.languageModelsSettings.models.forEach { modelSettings ->
            setupLanguageModelProvider(modelSettings)
        }
    }

    private fun AiProvidersClient.setupLanguageModelProvider(modelSettings: LanguageModelSettings) {
        when (modelSettings.provider) {
            LMProvider.OPENAI -> {
                log.debug("Using OpenAI language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val openAiConfig = OpenAIConfig(
                        token = modelSettings.providerKey,
                        organization = modelSettings.orgId.takeIf { it.isNotEmpty() },
                        logging = LoggingConfig(LogLevel.None),
                        engine = JavaHttpEngine(JavaHttpConfig())
                    )
                    val openAI = OpenAiClient(modelSettings.name, project, openAiConfig)
                    addLlmProvider(openAI)
                    addAssistantProvider(openAI)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.ANTHROPIC -> {
                log.debug("Using Anthropic language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val anthropicClient = AnthropicClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey
                    )
                    addLlmProvider(anthropicClient)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.TOGETHER_AI -> {
                log.debug("Using TogetherAI language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val togetherAiClient = TogetherAiClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey
                    )
                    addLlmProvider(togetherAiClient)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.HUGGING_FACE -> {
                log.debug("Using Hugging Face language model provider")
                val hasProviderKey = modelSettings.providerKey.isNotEmpty()
                val hasProviderEndpoint = modelSettings.apiUrl.isNotEmpty()
                if (hasProviderKey && hasProviderEndpoint) {
                    val huggingFaceClient = HuggingFaceClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey,
                        endpoint = modelSettings.apiUrl
                    )
                    addLlmProvider(huggingFaceClient)
                } else if (!hasProviderKey) {
                    log.warnChat("Missing language model provider key")
                } else {
                    log.warnChat("Missing language model endpoint url")
                }
            }

            LMProvider.MISTRAL_AI -> {
                log.debug("Using MistralAI language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val mistralAiClient = MistralAiClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey
                    )
                    addLlmProvider(mistralAiClient)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.GROQ -> {
                log.debug("Using Groq language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val groqClient = GroqClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey
                    )
                    addLlmProvider(groqClient)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.OLLAMA -> {
                log.debug("Using Ollama language model provider")
                if (modelSettings.apiUrl.isNotEmpty()) {
                    val ollamaClient = OllamaClient(
                        modelSettings.name,
                        project,
                        providerUrl = modelSettings.apiUrl
                    )
                    addLlmProvider(ollamaClient)
                } else {
                    log.warnChat("Missing language model endpoint url")
                }
            }

            LMProvider.CUSTOM -> {
                log.debug("Using custom language model provider")
                if (modelSettings.apiUrl.isNotEmpty()) {
                    val headers = toHeaderMap(modelSettings.apiHeaders)
                    val customConfig = OpenAIConfig(
                        token = modelSettings.providerKey,
                        organization = modelSettings.orgId.takeIf { it.isNotEmpty() },
                        logging = LoggingConfig(LogLevel.None),
                        engine = JavaHttpEngine(JavaHttpConfig()),
                        headers = headers,
                        host = OpenAIHost(baseUrl = modelSettings.apiUrl)
                    )
                    val customClient = OpenAiClient(modelSettings.name, project, customConfig)
                    addLlmProvider(customClient)
                } else {
                    log.warnChat("Missing language model endpoint url")
                }
            }

            LMProvider.VERTEX_AI -> {
                log.debug("Using Vertex AI language model provider")
                val vertexAiClient = VertexAiClient(
                    modelSettings.name,
                    project,
                    projectId = modelSettings.projectId,
                    location = modelSettings.location,
                )
                addLlmProvider(vertexAiClient)
                if (modelSettings.audioModality) {
                    log.debug("Vertex AI audio modality enabled")
                    addStmProvider(vertexAiClient)
                }
            }

            LMProvider.DEEPSEEK -> {
                log.debug("Using DeepSeek language model provider")
                val deepseekConfig = OpenAIConfig(
                    token = modelSettings.providerKey,
                    logging = LoggingConfig(LogLevel.None),
                    engine = JavaHttpEngine(JavaHttpConfig()),
                    host = OpenAIHost(baseUrl = "https://api.deepseek.com")
                )
                val deepseekClient = DeepSeekClient(modelSettings.name, project, deepseekConfig)
                addLlmProvider(deepseekClient)
            }

            LMProvider.GOOGLE_API -> {
                log.debug("Using Google API language model provider")
                val googleApiClient = GoogleApiClient(
                    modelSettings.name,
                    project,
                    providerKey = modelSettings.providerKey
                )
                addLlmProvider(googleApiClient)
                if (modelSettings.audioModality) {
                    log.debug("Google API audio modality enabled")
                    addStmProvider(googleApiClient)
                }
            }

            LMProvider.FIREWORKS_AI -> {
                log.debug("Using Fireworks language model provider")
                if (modelSettings.providerKey.isNotEmpty()) {
                    val fireworksClient = FireworksClient(
                        modelSettings.name,
                        project,
                        providerKey = modelSettings.providerKey
                    )
                    addLlmProvider(fireworksClient)
                } else {
                    log.warnChat("Missing language model provider key")
                }
            }

            LMProvider.NONE -> Unit //nop
        }
    }

    private fun AiProvidersClient.setupSpeechToTextProvider(voqalConfig: VoqalConfig) {
        when (voqalConfig.speechToTextSettings.provider) {
            SpeechToTextSettings.STTProvider.NONE -> {
                log.debug("No speech-to-text provider configured")
            }

            SpeechToTextSettings.STTProvider.PICOVOICE -> {
                log.debug("Using Picovoice speech-to-text provider")
                if (voqalConfig.speechToTextSettings.providerKey.isNotEmpty()) {
                    try {
                        addSttProvider(
                            PicovoiceLeopardClient(
                                project,
                                voqalConfig.speechToTextSettings.providerKey
                            )
                        )
                    } catch (e: PicovoiceError) {
                        log.warnChat("Unable to validate speech-to-text provider key")
                    }
                } else {
                    log.warnChat("Missing speech-to-text provider key")
                }
            }

            SpeechToTextSettings.STTProvider.ASSEMBLYAI -> {
                log.debug("Using AssemblyAI speech-to-text provider")
                if (voqalConfig.speechToTextSettings.providerKey.isNotEmpty()) {
                    addSttProvider(AssemblyAiClient(project, voqalConfig.speechToTextSettings.providerKey))
                } else {
                    log.warnChat("Missing speech-to-text provider key")
                }
            }

            SpeechToTextSettings.STTProvider.OPENAI -> {
                log.debug("Using OpenAI speech-to-text provider")
                if (voqalConfig.speechToTextSettings.providerKey.isNotEmpty()) {
                    val openAiConfig = OpenAIConfig(
                        token = voqalConfig.speechToTextSettings.providerKey,
                        organization = voqalConfig.speechToTextSettings.orgId.takeIf { it.isNotEmpty() },
                        logging = LoggingConfig(LogLevel.None),
                        engine = JavaHttpEngine(JavaHttpConfig())
                    )
                    val openAI = OpenAiClient("", project, openAiConfig)
                    addSttProvider(openAI)
                } else {
                    log.warnChat("Missing speech-to-text provider key")
                }
            }

            SpeechToTextSettings.STTProvider.WHISPER_ASR -> {
                log.debug("Using Whisper ASR speech-to-text provider")
                if (voqalConfig.speechToTextSettings.providerUrl.isNotEmpty()) {
                    val whisperAsrClient = WhisperClient(
                        providerUrl = voqalConfig.speechToTextSettings.providerUrl
                    )
                    addSttProvider(whisperAsrClient)
                } else {
                    log.warnChat("Missing speech-to-text provider url")
                }
            }

            SpeechToTextSettings.STTProvider.DEEPGRAM -> {
                log.debug("Using Deepgram speech-to-text provider")

                if (voqalConfig.speechToTextSettings.providerKey.isNotEmpty()) {
                    val deepgramClient = DeepgramClient(
                        project = project,
                        providerKey = voqalConfig.speechToTextSettings.providerKey,
                        isSttProvider = true
                    )
                    addSttProvider(deepgramClient)
                } else {
                    log.warnChat("Missing speech-to-text provider key")
                }
            }

            SpeechToTextSettings.STTProvider.GROQ -> {
                log.debug("Using Groq Whisper speech-to-text provider")
                val groqWhisperClient = GroqWhisperClient(
                    project = project,
                    providerKey = voqalConfig.speechToTextSettings.providerKey
                )
                addSttProvider(groqWhisperClient)
            }
        }
    }

    private fun AiProvidersClient.setupVoiceActivityProvider(voqalConfig: VoqalConfig) {
        when (voqalConfig.voiceDetectionSettings.provider) {
            VoiceDetectionSettings.VoiceDetectionProvider.Voqal -> {
                log.debug("Using Voqal voice activity detection provider")
                addVadProvider(
                    VoqalVadClient(
                        project,
                        voqalConfig.voiceDetectionSettings.sensitivity,
                        voqalConfig.voiceDetectionSettings.voiceSilenceThreshold,
                        voqalConfig.voiceDetectionSettings.speechSilenceThreshold,
                        voqalConfig.voiceDetectionSettings.sustainDuration
                    )
                )
            }

            VoiceDetectionSettings.VoiceDetectionProvider.Picovoice -> {
                log.debug("Using Picovoice voice activity detection provider")
                if (voqalConfig.voiceDetectionSettings.providerKey.isNotEmpty()) {
                    try {
                        addVadProvider(
                            PicovoiceCobraClient(
                                project,
                                voqalConfig.voiceDetectionSettings.providerKey,
                                voqalConfig.voiceDetectionSettings.sensitivity.toDouble(),
                                voqalConfig.voiceDetectionSettings.voiceSilenceThreshold,
                                voqalConfig.voiceDetectionSettings.speechSilenceThreshold,
                                voqalConfig.voiceDetectionSettings.sustainDuration
                            )
                        )
                    } catch (e: PicovoiceError) {
                        log.warnChat("Unable to validate wake provider key")
                    }
                } else {
                    log.warnChat("Missing wake provider key")
                }
            }

            VoiceDetectionSettings.VoiceDetectionProvider.None -> Unit //nop
        }
    }

    @VisibleForTesting
    fun setCachedConfig(config: VoqalConfig) {
        log.info("Set cached config to: ${config.withKeysRemoved()}")
        cachedConfig = config.copy()
        resetAiProvider()
    }

    fun getPromptSettings(promptName: String): PromptSettings {
        val config = getConfig()
        return config.promptLibrarySettings.prompts.first {
            it.promptName.equals(promptName, true)
        }
    }

    fun getCurrentPromptSettings(): PromptSettings {
        val configService = project.service<VoqalConfigService>()
        val promptName = when (project.service<VoqalStatusService>().getStatus()) {
            VoqalStatus.SEARCHING -> "Search Mode"
            VoqalStatus.EDITING -> "Edit Mode"
            else -> "Idle Mode"
        }
        return configService.getPromptSettings(promptName)
    }

    fun getLanguageModelSettings(promptSettings: PromptSettings): LanguageModelSettings {
        val config = getConfig()
        return config.languageModelsSettings.models.firstOrNull {
            it.name == promptSettings.languageModel
        } ?: LanguageModelSettings()
    }

    fun getPromptTemplate(promptSettings: PromptSettings): String {
        when (promptSettings.provider) {
            PromptSettings.PProvider.CUSTOM_URL -> {
                val url = promptSettings.promptUrl
                try {
                    return URL(url).readText()
                } catch (e: Exception) {
                    project.service<VoqalStatusService>().update(
                        VoqalStatus.ERROR,
                        "Failed to find custom url prompt: $url"
                    )
                }
            }

            PromptSettings.PProvider.CUSTOM_FILE -> {
                val fileLocation = promptSettings.promptFile
                try {
                    return File(fileLocation).readText()
                } catch (e: Exception) {
                    project.service<VoqalStatusService>().update(
                        VoqalStatus.ERROR,
                        "Failed to find custom file prompt: $fileLocation"
                    )
                }
            }

            PromptSettings.PProvider.CUSTOM_TEXT -> return promptSettings.promptText
            else -> Unit
        }

        return getPromptTemplate(promptSettings.promptName)
    }

    fun getPromptTemplate(promptName: String): String {
        val formattedName = promptName.lowercase().replace(" ", "-")
        val resourcePath = "/voqal-$formattedName.md"
        return try {
            Resources.getResource(VoqalConfigService::class.java, resourcePath)
                .readText()
                .replace("\r\n", "\n")
        } catch (e: Exception) {
            "Unknown prompt name: $promptName"
        }
    }

    fun getCurrentPromptMode(): String {
        var promptName = "Idle Mode"
        if (project.service<VoqalStatusService>().getStatus() == VoqalStatus.EDITING) {
            promptName = "Edit Mode"
        } else if (project.service<VoqalStatusService>().getStatus() == VoqalStatus.SEARCHING) {
            promptName = "Search Mode"
        }
        return promptName
    }

    companion object {
        fun toHeaderMap(headerStr: String): Map<String, String> {
            val headers = mutableMapOf<String, String>()
            headerStr.split(",").filter { it.isNotEmpty() }.forEach {
                val (key, value) = it.split(":")
                headers[key] = value
            }
            return headers
        }
    }
}
