package dev.voqal.provider.clients.anthropic

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
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

class AnthropicClient(
    override val name: String,
    project: Project,
    private val providerKey: String
) : LlmProvider {

    companion object {
        const val DEFAULT_MODEL = "claude-3-5-sonnet-20240620"

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when (modelName) {
                "claude-3-5-sonnet-20240620" -> 200_000
                "claude-3-opus-20240229" -> 200_000
                "claude-3-sonnet-20240229" -> 200_000
                "claude-3-haiku-20240307" -> 200_000
                else -> -1
            }
        }

        @JvmStatic
        val MODELS = listOf(
            "claude-3-5-sonnet-20240620",
            "claude-3-opus-20240229",
            "claude-3-sonnet-20240229",
            "claude-3-haiku-20240307"
        )
    }

    private val log = project.getVoqalLogger(this::class)
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(ContentNegotiation) { json(jsonDecoder) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val providerUrl = "https://api.anthropic.com/v1/messages"

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val requestJson = toRequestJson(request)

        val response = try {
            client.post(providerUrl) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("x-api-key", providerKey)
                header("anthropic-version", "2023-06-01")
                setBody(requestJson.encode())
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
        val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
        log.debug("Anthropic response status: ${response.status} in $roundTripTime ms")

        throwIfError(response)
        val body = JsonObject(response.bodyAsText())
        val choice = ChatChoice(
            index = 0, //todo: multiple choices
            ChatMessage(
                ChatRole.Assistant,
                TextContent(body.getJsonArray("content").getJsonObject(0).getString("text"))
            )
        )
        val completion = ChatCompletion(
            id = body.getString("id"),
            created = System.currentTimeMillis(),
            model = ModelId(body.getString("model")),
            choices = listOf(choice),
            usage = toUsage(body.getJsonObject("usage"))
        )
        return completion
    }

    override suspend fun streamChatCompletion(
        request: ChatCompletionRequest,
        directive: VoqalDirective?
    ): Flow<ChatCompletionChunk> = flow {
        val requestJson = toRequestJson(request).put("stream", true)

        try {
            client.preparePost(providerUrl) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("x-api-key", providerKey)
                header("anthropic-version", "2023-06-01")
                setBody(requestJson.encode())
            }.execute { response ->
                throwIfError(response)

                val messageData = JsonObject()
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

                    if (line.startsWith("event:")) {
                        continue
                    }

                    val data = JsonObject(line.substringAfter("data: "))
                    when (data.getString("type")) {
                        "message_start" -> {
                            messageData.mergeIn(data.getJsonObject("message"))
                            continue
                        }

                        "content_block_start" -> continue
                        "content_block_delta" -> Unit //fall through

                        "message_delta" -> {
                            messageData.mergeIn(data.getJsonObject("delta"))
                            messageData.getJsonObject("usage").mergeIn(data.getJsonObject("usage"))
                            Unit //fall through
                        }

                        "content_block_stop" -> continue
                        "message_stop" -> continue
                        "ping" -> continue
                        else -> {
                            log.warn("Unknown data type: " + data.getString("type"))
                            continue
                        }
                    }

                    val dataDelta = data.getJsonObject("delta")
                    emit(
                        ChatCompletionChunk(
                            id = messageData.getString("id"),
                            created = 0, //todo: System.currentTimeMillis(),
                            model = ModelId(messageData.getString("model")),
                            choices = listOf(
                                ChatChunk(
                                    index = 0,
                                    delta = ChatDelta(
                                        role = toRole(messageData),
                                        content = dataDelta.getString("text") ?: ""
                                    ),
                                    finishReason = toFinishReason(messageData)
                                )
                            ),
                            usage = toUsage(messageData.getJsonObject("usage"))
                        )
                    )
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    private fun toRequestJson(request: ChatCompletionRequest): JsonObject {
        val maxTokens = 4096 //todo: configurable
        val requestJson = JsonObject()
            .put("model", request.model.id)
            .put("messages", JsonArray(request.messages.map { it.toJson() }))
            .put("max_tokens", maxTokens)
        return requestJson
    }

    private suspend fun throwIfError(response: HttpResponse) {
        if (response.status.isSuccess()) return

        if (response.status.value == 401) {
            throw AuthenticationException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = "Unauthorized access to Anthropic. Please check your API key and try again.",
                        param = null,
                        type = null
                    )
                ),
                ClientRequestException(response, response.bodyAsText())
            )
        } else if (response.status.value == 429) {
            var errorMessage = "Rate limit exceeded. Please try again later."
            try {
                val jsonBody = JsonObject(response.bodyAsText())
                if (jsonBody.containsKey("error")) {
                    errorMessage = jsonBody.getJsonObject("error").getString("message")
                }
            } catch (_: Exception) {
            }
            throw RateLimitException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = errorMessage,
                        param = null,
                        type = null
                    )
                ),
                ClientRequestException(response, response.bodyAsText())
            )
        }

        var errorMessage = "Invalid request to Anthropic. Please check your request and try again."
        try {
            val jsonBody = JsonObject(response.bodyAsText())
            if (jsonBody.containsKey("error")) {
                errorMessage = jsonBody.getJsonObject("error").getString("message")
            }
        } catch (_: Exception) {
        }

        log.warn("Anthropic completion failed: ${response.status}. Message: ${response.bodyAsText()}")
        throw InvalidRequestException(
            response.status.value,
            OpenAIError(
                OpenAIErrorDetails(
                    code = null,
                    message = errorMessage,
                    param = null,
                    type = null
                )
            ),
            ClientRequestException(response, response.bodyAsText())
        )
    }

    private fun toFinishReason(messageData: JsonObject): FinishReason? {
        return when (messageData.getString("stop_reason")) {
            null -> null
            "stop" -> FinishReason.Stop
            "length" -> FinishReason.Length
            "function_call" -> FinishReason.FunctionCall
            "tool_calls" -> FinishReason.ToolCalls
            "content_filter" -> FinishReason.ContentFilter
            else -> FinishReason(messageData.getString("stop_reason"))
        }
    }

    private fun toRole(messageData: JsonObject): Role {
        return when (messageData.getString("role")) {
            "system" -> Role.System
            "user" -> Role.User
            "assistant" -> Role.Assistant
            "function" -> Role.Function
            "tool" -> Role.Tool
            else -> Role(messageData.getString("role"))
        }
    }

    private fun toUsage(json: JsonObject): Usage {
        return Usage(
            promptTokens = json.getInteger("input_tokens"),
            completionTokens = json.getInteger("output_tokens"),
            totalTokens = json.getInteger("input_tokens") + json.getInteger("output_tokens")
        )
    }

    override fun isStreamable() = true
    override fun getAvailableModelNames() = MODELS
    override fun dispose() = client.close()

    private fun ChatMessage.toJson(): JsonObject {
        val roleName = when (role) {
            ChatRole.System -> "user" //todo: claude uses system field in req instead of role
            else -> role.role.lowercase()
        }
        return JsonObject().put("role", roleName).put("content", content)
    }
}
