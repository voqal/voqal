package dev.voqal.assistant

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.chat.ToolCall
import dev.voqal.services.getVoqalLogger

/**
 * Represents a fully processed LLM response ready for execution.
 */
data class VoqalResponse(
    val directive: VoqalDirective,
    val toolCalls: List<ToolCall>,
    val backingResponse: ChatCompletion? = null,
    val exception: Throwable? = null
) {
    companion object {
        fun calculateTotalPrice(model: String, incomingTokens: Int, outgoingTokens: Int): Double {
            val modelName = PRICE_TABLE.keys.find { it.contains(model) }
            val priceMap = PRICE_TABLE[modelName]!!
            return ((priceMap[0] ?: 0.0) * (outgoingTokens / 1000.0)) + ((priceMap[1]
                ?: 0.0) * (incomingTokens / 1000.0))
        }

        private val PRICE_TABLE = mapOf(
            "mistral-medium-latest" to mapOf(0 to 0.0027, 1 to 0.0081),
            "mistral-large-latest" to mapOf(0 to 0.008, 1 to 0.024),
            "gpt-4-turbo-2024-04-09" to mapOf(0 to 0.01, 1 to 0.03),
            "gpt-4-0125-preview" to mapOf(0 to 0.01, 1 to 0.03),
            "gpt-4-1106-preview" to mapOf(0 to 0.01, 1 to 0.03),
            "gpt-4" to mapOf(0 to 0.03, 1 to 0.06),
            "gpt-4o" to mapOf(0 to 0.005, 1 to 0.015),
            "gpt-4o-2024-05-13" to mapOf(0 to 0.005, 1 to 0.015),
            "gpt-4o-2024-08-06" to mapOf(0 to 0.0025, 1 to 0.01),
            "gpt-4o-mini" to mapOf(0 to 0.00015, 1 to 0.0006),
            "gpt-4o-mini-2024-07-18" to mapOf(0 to 0.00015, 1 to 0.0006),
            "gpt-4-0613" to mapOf(0 to 0.03, 1 to 0.06),
            "gpt-4-32k" to mapOf(0 to 0.06, 1 to 0.12),
            "gpt-3.5-turbo-16k" to mapOf(0 to 0.003, 1 to 0.004),
            "gpt-3.5-turbo-16k-0613" to mapOf(0 to 0.003, 1 to 0.004),
            "gpt-3.5-turbo-0613" to mapOf(0 to 0.0015, 1 to 0.002),
            "gpt-3.5-turbo-1106" to mapOf(0 to 0.001, 1 to 0.002),
            "gpt-3.5-turbo-0125" to mapOf(0 to 0.0005, 1 to 0.0015),
            "gpt-3.5-turbo" to mapOf(0 to 0.0015, 1 to 0.002),
            "meta-llama/meta-llama-3.1-8b-instruct-turbo" to mapOf(0 to 0.00018, 1 to 0.00018),
            "meta-llama/Meta-Llama-3.1-8B-Instruct-Turbo-vLLM" to mapOf(0 to 0.00018, 1 to 0.00018),
            "meta-llama/meta-llama-3.1-70b-instruct-turbo" to mapOf(0 to 0.00088, 1 to 0.00088),
            "meta-llama/Meta-Llama-3.1-70B-Instruct-Turbo-vLLM" to mapOf(0 to 0.00088, 1 to 0.00088),
            "meta-llama/meta-llama-3.1-405b-instruct-turbo" to mapOf(0 to 0.005, 1 to 0.005),
            "meta-llama/Meta-Llama-3.1-405B-Instruct-Turbo-vLLM" to mapOf(0 to 0.005, 1 to 0.005),
            "codestral-2405" to mapOf(0 to 0.001, 1 to 0.003),
            "codestral-latest" to mapOf(0 to 0.001, 1 to 0.003),
            "gemini-1.5-flash-latest" to mapOf(0 to 0.00035, 1 to 0.00105), //todo: increases after 1 million tokens
            "gemini-1.5-pro-latest" to mapOf(0 to 0.0035, 1 to 0.0105), //todo: increases after 1 million tokens
            "gemini-1.5-pro-exp-0801" to mapOf(0 to 0.0035, 1 to 0.0105), //todo: increases after 1 million tokens
            "claude-3-sonnet-20240229" to mapOf(0 to 0.003, 1 to 0.015),
            "claude-3-5-sonnet-20240620" to mapOf(0 to 0.003, 1 to 0.015),
            "mistral-small-latest" to mapOf(0 to 0.001, 1 to 0.003),
            "mistral-medium-latest" to mapOf(0 to 0.00275, 1 to 0.0081),
            "open-mistral-nemo" to mapOf(0 to 0.0003, 1 to 0.0003),
        )
    }

    fun getSpentCurrency(): Double {
        try {
            return calculateTotalPrice(
                backingResponse!!.model.id,
                backingResponse.usage!!.promptTokens!!,
                backingResponse.usage!!.completionTokens!!
            )
        } catch (e: Exception) {
            val log = directive.project.getVoqalLogger(this::class)
            log.warn("Model " + backingResponse!!.model.id + " not found in the price table")
            return -1.0
        }
    }

    fun getBackingResponseAsText(): String {
        require(backingResponse != null)
        require(backingResponse.choices.size == 1)
        val messageContent = backingResponse.choices.first().message.messageContent
        return if (messageContent is TextContent) {
            messageContent.content
        } else {
            messageContent.toString()
        }
    }
}
