package dev.voqal.provider.clients.anthropic

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.provider.LlmProvider
import dev.voqal.services.getVoqalLogger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json

class AnthropicClient(
    override val name: String,
    private val project: Project,
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

    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val url = "https://api.anthropic.com/v1/messages"

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val log = project.getVoqalLogger(this::class)
        try {
            val maxTokens = 4096 //todo: configurable
            val requestJson = JsonObject()
                .put("model", request.model.id)
                .put("messages", JsonArray(request.messages.map { it.toJson() }))
                .put("max_tokens", maxTokens)
            val response = client.post(url) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("x-api-key", providerKey)
                header("anthropic-version", "2023-06-01")
                setBody(requestJson.encode())
            }
            val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
            log.debug("Anthropic response status: ${response.status} in $roundTripTime ms")

            if (response.status.isSuccess()) {
                val json = JsonObject(response.bodyAsText())
                val choice = ChatChoice(
                    index = 0, //todo: multiple choices
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(json.getJsonArray("content").getJsonObject(0).getString("text"))
                    )
                )
                val completion = ChatCompletion(
                    id = json.getString("id"),
                    created = System.currentTimeMillis(),
                    model = ModelId(json.getString("model")),
                    choices = listOf(choice),
                    usage = Usage(
                        promptTokens = json.getJsonObject("usage").getInteger("input_tokens"),
                        completionTokens = json.getJsonObject("usage").getInteger("output_tokens"),
                        totalTokens = 0
                    ).let { it.copy(totalTokens = it.promptTokens!! + it.completionTokens!!) }
                )
                return completion
            } else if (response.status.value == 401) {
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
            } else {
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
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

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
