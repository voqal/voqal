package dev.voqal.provider.clients

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import dev.voqal.provider.*
import dev.voqal.services.VoqalConfigService

class AiProvidersClient(private val project: Project) : AiProvider {

    private val vadProviders = mutableListOf<VadProvider>()
    private val llmProviders = mutableListOf<LlmProvider>()
    private val sttProviders = mutableListOf<SttProvider>()
    private val ttsProviders = mutableListOf<TtsProvider>()
    private val assistantProviders = mutableListOf<AssistantProvider>()
    private val observabilityProviders = mutableListOf<ObservabilityProvider>()
    private val stmProviders = mutableListOf<StmProvider>()

    fun addVadProvider(provider: VadProvider) {
        vadProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addLlmProvider(provider: LlmProvider) {
        llmProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addSttProvider(provider: SttProvider) {
        sttProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addTtsProvider(provider: TtsProvider) {
        ttsProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addAssistantProvider(provider: AssistantProvider) {
        assistantProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addObservabilityProvider(provider: ObservabilityProvider) {
        observabilityProviders.add(provider)
        Disposer.register(this, provider)
    }

    fun addStmProvider(provider: StmProvider) {
        stmProviders.add(provider)
        Disposer.register(this, provider)
    }

    override fun isVadProvider(): Boolean {
        return vadProviders.any { it.isVadProvider() }
    }

    override fun asVadProvider(): VadProvider {
        return vadProviders.first { it.isVadProvider() }.asVadProvider()
    }

    override fun isLlmProvider(): Boolean {
        return llmProviders.any { it.isLlmProvider() }
    }

    override fun asLlmProvider(): LlmProvider {
        return llmProviders.first { it.isLlmProvider() }.asLlmProvider()
    }

    override fun asLlmProvider(name: String): LlmProvider {
        return llmProviders.first { it.name == name }
    }

    override fun isSttProvider(): Boolean {
        return sttProviders.any { it.isSttProvider() }
    }

    override fun asSttProvider(): SttProvider {
        return sttProviders.first { it.isSttProvider() }.asSttProvider()
    }

    override fun isTtsProvider(): Boolean {
        return ttsProviders.any { it.isTtsProvider() }
    }

    override fun asTtsProvider(): TtsProvider {
        return ttsProviders.first { it.isTtsProvider() }.asTtsProvider()
    }

    override fun isAssistantProvider(): Boolean {
        return assistantProviders.any { it.isAssistantProvider() }
    }

    override fun asAssistantProvider(): AssistantProvider {
        return assistantProviders.first { it.isAssistantProvider() }.asAssistantProvider()
    }

    override fun isObservabilityProvider(): Boolean {
        return observabilityProviders.any { it.isObservabilityProvider() }
    }

    override fun asObservabilityProvider(): ObservabilityProvider {
        return observabilityProviders.first { it.isObservabilityProvider() }.asObservabilityProvider()
    }

    override fun isStmProvider(): Boolean {
        val promptSettings = project.service<VoqalConfigService>().getCurrentPromptSettings()
        return stmProviders
            .filter { it.name == promptSettings.languageModel }
            .any { it.isStmProvider() }
    }

    override fun asStmProvider(): StmProvider {
        val promptSettings = project.service<VoqalConfigService>().getCurrentPromptSettings()
        return stmProviders
            .filter { it.name == promptSettings.languageModel }
            .first { it.isStmProvider() }.asStmProvider()
    }

    override fun findProvider(name: String): AiProvider? {
        return llmProviders.find { it.name == name }
            ?: stmProviders.find { it.name == name }
    }

    fun hasNecessaryProviders(): Boolean {
        return llmProviders.isNotEmpty() ||
                sttProviders.isNotEmpty() ||
                ttsProviders.isNotEmpty() ||
                assistantProviders.isNotEmpty() ||
                observabilityProviders.isNotEmpty() ||
                stmProviders.isNotEmpty()
    }

    override fun dispose() = Unit
}
