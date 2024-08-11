package dev.voqal.provider

import com.intellij.openapi.Disposable

/**
 * Provider that offers various AI services.
 */
interface AiProvider : Disposable {

    fun isVadProvider(): Boolean = false
    fun asVadProvider(): VadProvider {
        return this as VadProvider
    }

    fun isLlmProvider(): Boolean = false
    fun asLlmProvider(): LlmProvider {
        return this as LlmProvider
    }

    fun asLlmProvider(name: String): LlmProvider {
        throw NotImplementedError("asLlmProvider(name: String) is not implemented")
    }

    fun isSttProvider(): Boolean = false
    fun asSttProvider(): SttProvider {
        return this as SttProvider
    }

    fun isTtsProvider(): Boolean = false
    fun asTtsProvider(): TtsProvider {
        return this as TtsProvider
    }

    fun isAssistantProvider(): Boolean = false
    fun asAssistantProvider(): AssistantProvider {
        return this as AssistantProvider
    }

    fun isObservabilityProvider(): Boolean = false
    fun asObservabilityProvider(): ObservabilityProvider {
        return this as ObservabilityProvider
    }

    fun isStmProvider(): Boolean = false
    fun asStmProvider(): StmProvider {
        return this as StmProvider
    }
}
