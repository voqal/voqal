package dev.voqal.provider

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import dev.voqal.assistant.VoqalDirective

/**
 * Provider that offers LLM.
 */
interface LlmProvider : AiProvider {
    val name: String
    override fun isLlmProvider() = true
    suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective? = null): ChatCompletion
    fun getAvailableModelNames(): List<String>
}
