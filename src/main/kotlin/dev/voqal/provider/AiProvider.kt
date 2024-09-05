package dev.voqal.provider

import com.intellij.openapi.Disposable

/**
 * Provider that offers various AI services.
 */
interface AiProvider : Disposable {
    fun isVadProvider(): Boolean = false
    fun asVadProvider(): VadProvider = this as VadProvider

    fun isLlmProvider(): Boolean = false
    fun asLlmProvider(): LlmProvider = this as LlmProvider

    fun asLlmProvider(name: String): LlmProvider =
        throw NotImplementedError("asLlmProvider(name: String) is not implemented")

    fun isSttProvider(): Boolean = false
    fun asSttProvider(): SttProvider = this as SttProvider

    fun isTtsProvider(): Boolean = false
    fun asTtsProvider(): TtsProvider = this as TtsProvider

    fun isAssistantProvider(): Boolean = false
    fun asAssistantProvider(): AssistantProvider = this as AssistantProvider

    fun isObservabilityProvider(): Boolean = false
    fun asObservabilityProvider(): ObservabilityProvider = this as ObservabilityProvider

    fun isStmProvider(): Boolean = false
    fun asStmProvider(): StmProvider = this as StmProvider
}
