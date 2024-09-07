package dev.voqal.provider.clients.mistralai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.exception.*
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.provider.LlmProvider
import dev.voqal.services.getVoqalLogger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class MistralAiClient(
    override val name: String,
    project: Project,
    private val providerKey: String
) : LlmProvider {

    companion object {
        const val DEFAULT_MODEL = "mistral-medium-latest"

        @JvmStatic
        val MODELS = listOf(
            "codestral-latest",
            "open-mistral-nemo",
            "mistral-large-latest",
            "mistral-medium-latest",
            "mistral-small-latest",
            "open-mixtral-8x22b",
            "open-mixtral-8x7b",
            "open-mistral-7b"
        )

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when (modelName) {
                "open-mistral-7b" -> 32_000
                "open-mixtral-8x7b" -> 32_000
                "open-mixtral-8x22b" -> 64_000
                "mistral-small-latest" -> 32_000
                "mistral-medium-latest" -> 32_000
                "mistral-large-latest" -> 128_000
                "open-mistral-nemo" -> 128_000
                "open-mistral-nemo-2407" -> 128_000
                "codestral-latest" -> 32_000
                else -> -1
            }
        }
    }

    private val log = project.getVoqalLogger(this::class)
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(ContentNegotiation) { json(jsonDecoder) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val providerUrl = "https://api.mistral.ai/v1/chat/completions"

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val requestJson = JsonObject()
            .put("model", request.model.id)
            .put("messages", JsonArray(request.messages.map { it.toJson() }))

        val response = try {
            client.post(providerUrl) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("Authorization", "Bearer $providerKey")
                setBody(requestJson.encode())
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
        val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
        log.debug("Mistral response status: ${response.status} in $roundTripTime ms")

        throwIfError(response)
        val completion = response.body<ChatCompletion>()
        log.debug("Completion: $completion")
        return completion
    }

    override suspend fun streamChatCompletion(
        request: ChatCompletionRequest,
        directive: VoqalDirective?
    ): Flow<ChatCompletionChunk> = flow {
        val requestJson = JsonObject()
            .put("model", request.model.id)
            .put("messages", JsonArray(request.messages.map { it.toJson() }))
            .put("stream", true)

        try {
            client.preparePost(providerUrl) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("Authorization", "Bearer $providerKey")
                setBody(requestJson.encode())
            }.execute { response ->
                throwIfError(response)

                var hasError = false
                val channel: ByteReadChannel = response.body()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line()?.takeUnless { it.isEmpty() } ?: continue

                    if (line == "event: error") {
                        hasError = true
                        continue
                    } else if (hasError) {
                        val errorJson = JsonObject(line.substringAfter("data: "))
                        log.warn("Received error while streaming completions: $errorJson")

                        val statusCode = errorJson.getJsonObject("error").getInteger("status_code")
                        throw UnknownAPIException(statusCode, jsonDecoder.decodeFromString(errorJson.toString()))
                    }

                    val chunkJson = line.substringAfter("data: ")
                    if (chunkJson != "[DONE]") {
                        emit(jsonDecoder.decodeFromString(chunkJson))
                    }
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    private suspend fun throwIfError(response: HttpResponse) {
        if (response.status.isSuccess()) return

        if (response.status.value == 401) {
            throw AuthenticationException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = "Unauthorized access to Mistral AI. Please check your API key and try again.",
                        param = null,
                        type = null
                    )
                ),
                ClientRequestException(response, response.bodyAsText())
            )
        } else if (response.status.value == 429) {
            throw RateLimitException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = "Rate limit exceeded. Please try again later.",
                        param = null,
                        type = null
                    )
                ),
                ClientRequestException(response, response.bodyAsText())
            )
        }

        log.error("Mistral completion failed: ${response.status}")
        throw Exception("Mistral completion failed: ${response.status}")
    }

    override fun isStreamable() = true
    override fun getAvailableModelNames() = MODELS
    override fun dispose() = client.close()
    private fun ChatMessage.toJson() = JsonObject().put("role", "user").put("content", content)
}
