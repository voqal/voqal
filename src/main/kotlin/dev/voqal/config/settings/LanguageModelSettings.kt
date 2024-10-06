package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import dev.voqal.ide.VoqalIcons
import dev.voqal.provider.clients.anthropic.AnthropicClient
import dev.voqal.provider.clients.azure.AzureClient
import dev.voqal.provider.clients.cerebras.CerebrasClient
import dev.voqal.provider.clients.deepseek.DeepSeekClient
import dev.voqal.provider.clients.fireworks.FireworksClient
import dev.voqal.provider.clients.googleapi.GoogleApiClient
import dev.voqal.provider.clients.groq.GroqClient
import dev.voqal.provider.clients.mistralai.MistralAiClient
import dev.voqal.provider.clients.openai.OpenAiClient
import dev.voqal.provider.clients.sambanova.SambaNovaClient
import dev.voqal.provider.clients.togetherai.TogetherAiClient
import dev.voqal.provider.clients.vertexai.VertexAiClient
import io.vertx.core.json.JsonObject
import javax.swing.Icon

data class LanguageModelSettings(
    val provider: LMProvider = LMProvider.NONE,
    val providerKey: String = "",
    val orgId: String = "",
    val modelName: String = "",
    val seed: Int? = null,
    val temperature: Double? = null,
    val observabilityProvider: OProvider = OProvider.None,
    val observabilityKey: String = "",
    val observabilityUserId: String = "",
    val apiUrl: String = "",
    val tokenLimit: Int = -1,
    val apiHeaders: String = "",
    val projectId: String = "",
    val location: String = "",
    val name: String = provider.displayName,
    val audioModality: Boolean = false
) : ConfigurableSettings {

    companion object {
        @JvmStatic
        fun duplicate(settings: LanguageModelSettings, name: String): LanguageModelSettings {
            return settings.copy(name = name)
        }

        @JvmStatic
        fun asDefault(provider: LMProvider): LanguageModelSettings {
            return when (provider) {
                LMProvider.OPENAI -> LanguageModelSettings(provider, modelName = OpenAiClient.DEFAULT_MODEL)
                LMProvider.GOOGLE_API -> LanguageModelSettings(provider, modelName = GoogleApiClient.DEFAULT_MODEL)
                LMProvider.ANTHROPIC -> LanguageModelSettings(provider, modelName = AnthropicClient.DEFAULT_MODEL)
                LMProvider.DEEPSEEK -> LanguageModelSettings(provider, modelName = DeepSeekClient.DEFAULT_MODEL)
                LMProvider.GROQ -> LanguageModelSettings(provider, modelName = GroqClient.DEFAULT_MODEL)
                LMProvider.FIREWORKS_AI -> LanguageModelSettings(provider, modelName = FireworksClient.DEFAULT_MODEL)
                LMProvider.TOGETHER_AI -> LanguageModelSettings(provider, modelName = TogetherAiClient.DEFAULT_MODEL)
                LMProvider.MISTRAL_AI -> LanguageModelSettings(provider, modelName = MistralAiClient.DEFAULT_MODEL)
                LMProvider.VERTEX_AI -> LanguageModelSettings(provider, modelName = VertexAiClient.DEFAULT_MODEL)
                LMProvider.SAMBANOVA -> LanguageModelSettings(provider, modelName = SambaNovaClient.DEFAULT_MODEL)
                LMProvider.CEREBRAS -> LanguageModelSettings(provider, modelName = CerebrasClient.DEFAULT_MODEL)
                LMProvider.AZURE -> LanguageModelSettings(provider, modelName = AzureClient.DEFAULT_MODEL)

                LMProvider.OLLAMA -> LanguageModelSettings(provider, modelName = "")
                LMProvider.HUGGING_FACE -> LanguageModelSettings(provider, modelName = "")
                LMProvider.CUSTOM -> LanguageModelSettings(provider, modelName = "")
                LMProvider.NONE -> LanguageModelSettings(provider, modelName = "")
            }
        }
    }

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        provider = LMProvider.lenientValueOf(json.getString("provider") ?: LMProvider.NONE.name),
        providerKey = json.getString("providerKey", ""),
        orgId = json.getString("orgId", ""),
        modelName = json.getString("modelName", ""),
        seed = json.getInteger("seed"),
        temperature = json.getDouble("temperature"),
        observabilityProvider = OProvider.valueOf(json.getString("observabilityProvider") ?: OProvider.None.name),
        observabilityKey = json.getString("observabilityKey", ""),
        observabilityUserId = json.getString("observabilityUserId", ""),
        apiUrl = json.getString("apiUrl", ""),
        tokenLimit = json.getInteger("tokenLimit", -1),
        apiHeaders = json.getString("apiHeaders", ""),
        projectId = json.getString("projectId", ""),
        location = json.getString("location", ""),
        name = json.getString("name", LMProvider.NONE.displayName),
        audioModality = json.getBoolean("audioModality", false)
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("provider", provider.name)
            put("providerKey", providerKey)
            put("orgId", orgId)
            put("modelName", modelName)
            seed?.let { put("seed", it) }
            temperature?.let { put("temperature", it) }
            put("observabilityProvider", observabilityProvider.name)
            put("observabilityKey", observabilityKey)
            put("observabilityUserId", observabilityUserId)
            put("apiUrl", apiUrl)
            put("tokenLimit", tokenLimit)
            put("apiHeaders", apiHeaders)
            put("projectId", projectId)
            put("location", location)
            put("name", name)
            put("audioModality", audioModality)
        }
    }

    override fun withKeysRemoved(): LanguageModelSettings {
        return copy(
            providerKey = if (providerKey == "") "" else "***",
            orgId = if (orgId == "") "" else "***",
            observabilityKey = if (observabilityKey == "") "" else "***",
            apiHeaders = if (apiHeaders == "") "" else "***"
        )
    }

    override fun withPiiRemoved(): LanguageModelSettings {
        return withKeysRemoved().copy(
            seed = null,
            temperature = null,
            observabilityUserId = if (observabilityUserId == "unknown") "" else "***",
            apiUrl = if (apiUrl.isEmpty()) "" else "***",
            apiHeaders = if (apiHeaders.isEmpty()) "" else "***",
            projectId = if (projectId.isEmpty()) "" else "***",
            location = if (location.isEmpty()) "" else "***",
            name = "***"
        )
    }

    enum class LMProvider(val displayName: String) {
        NONE("None"),
        AZURE("Azure"),
        OPENAI("OpenAI"),
        GOOGLE_API("Google API"),
        SAMBANOVA("SambaNova"),
        CEREBRAS("Cerebras"),
        ANTHROPIC("Anthropic"),
        TOGETHER_AI("TogetherAI"),
        MISTRAL_AI("MistralAI"),
        GROQ("Groq"),
        OLLAMA("Ollama"),
        HUGGING_FACE("Hugging Face"),
        VERTEX_AI("Vertex AI"),
        DEEPSEEK("DeepSeek"),
        FIREWORKS_AI("Fireworks AI"),
        CUSTOM("Custom");

        fun getIcon(): Icon? {
            return when {
                this == OLLAMA -> VoqalIcons.Compute.ollama
                this == GOOGLE_API || this == VERTEX_AI -> VoqalIcons.Compute.google
                this == OPENAI -> VoqalIcons.Compute.openai
                this == HUGGING_FACE -> VoqalIcons.Compute.huggingface
                this == MISTRAL_AI -> VoqalIcons.Compute.mistralai
                this == GROQ -> VoqalIcons.Compute.groq
                this == TOGETHER_AI -> VoqalIcons.Compute.t
                this == CUSTOM -> VoqalIcons.Compute.globe
                this == ANTHROPIC -> VoqalIcons.Compute.anthropic
                this == DEEPSEEK -> VoqalIcons.Compute.deepseek
                this == FIREWORKS_AI -> VoqalIcons.Compute.fireworks
                this == SAMBANOVA -> VoqalIcons.Compute.sambanova
                this == CEREBRAS -> VoqalIcons.Compute.cerebras
                this == AZURE -> VoqalIcons.Compute.azure
                else -> null
            }
        }

        fun isKeyRequired(): Boolean {
            return this !in setOf(OLLAMA, VERTEX_AI)
        }

        fun isOrgIdAvailable(): Boolean {
            return this in setOf(OPENAI)
        }

        fun isModelNameRequired(): Boolean {
            return this in setOf(
                OPENAI, TOGETHER_AI, MISTRAL_AI,
                GROQ, OLLAMA, CUSTOM,
                VERTEX_AI, GOOGLE_API, ANTHROPIC,
                DEEPSEEK, FIREWORKS_AI, SAMBANOVA,
                CEREBRAS, AZURE
            )
        }

        fun isHeaderParamsAvailable(): Boolean {
            return this == CUSTOM
        }

        fun isApiUrlRequired(): Boolean {
            return this in setOf(HUGGING_FACE, OLLAMA, CUSTOM, AZURE)
        }

        fun isProjectIdRequired(): Boolean {
            return this in setOf(VERTEX_AI)
        }

        fun isLocationRequired(): Boolean {
            return this in setOf(VERTEX_AI)
        }

        fun isAudioModalityAvailable(): Boolean {
            return this in setOf(VERTEX_AI, GOOGLE_API, OPENAI, AZURE) //todo: actually per model name
        }

        companion object {
            @JvmStatic
            fun lenientValueOf(str: String): LMProvider {
                val str = str.replace(" ", "_")
                val findByDisplayName = entries.find { it.displayName.equals(str, true) }
                if (findByDisplayName != null) return findByDisplayName
                return LMProvider.valueOf(str.uppercase())
            }
        }
    }

    enum class OProvider {
        None,
        Helicone;

        fun isKeyRequired(): Boolean {
            return this == Helicone
        }
    }
}
