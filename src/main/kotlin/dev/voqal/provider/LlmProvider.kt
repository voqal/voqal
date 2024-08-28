package dev.voqal.provider

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import dev.voqal.assistant.VoqalDirective
import kotlinx.coroutines.flow.Flow

/**
 * Provider that offers LLM.
 */
interface LlmProvider : AiProvider {
    val name: String
    override fun isLlmProvider() = true
    fun isStreamable(): Boolean = false

    suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective? = null): ChatCompletion
    suspend fun streamChatCompletion(
        request: ChatCompletionRequest,
        directive: VoqalDirective? = null
    ): Flow<ChatCompletionChunk> = throw NotImplementedError()

    fun getAvailableModelNames(): List<String>
}
