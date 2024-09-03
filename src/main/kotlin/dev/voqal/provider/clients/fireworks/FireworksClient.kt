package dev.voqal.provider.clients.fireworks

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
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
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json

class FireworksClient(
    override val name: String,
    private val project: Project,
    private val providerKey: String
) : LlmProvider {

    companion object {
        const val DEFAULT_MODEL = "accounts/fireworks/models/llama-v3p1-405b-instruct"

        @JvmStatic
        val MODELS = listOf(
            "accounts/fireworks/models/llama-v3p1-405b-instruct",
            "accounts/fireworks/models/llama-v3p1-70b-instruct",
            "accounts/fireworks/models/llama-v3p1-8b-instruct",
            "accounts/fireworks/models/gemma2-9b-it"
        )

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when {
                modelName.startsWith("accounts/fireworks/models/llama-v3p1") -> 128_000
                modelName.endsWith("gemma2-9b-it") -> 8192
                else -> -1
            }
        }
    }

    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val url = "https://api.fireworks.ai/inference/v1/chat/completions"

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val log = project.getVoqalLogger(this::class)
        try {
            val requestJson = JsonObject()
                .put("model", request.model.id)
                .put("messages", JsonArray(request.messages.map { it.toJson() }))
            val response = client.post(url) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("Authorization", "Bearer $providerKey")
                setBody(requestJson.encode())
            }
            val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
            log.debug("Fireworks response status: ${response.status} in $roundTripTime ms")

            if (response.status.isSuccess()) {
                val completion = response.body<ChatCompletion>()
                log.debug("Completion: $completion")
                return completion
            } else if (response.status.value == 401) {
                throw AuthenticationException(
                    response.status.value,
                    OpenAIError(
                        OpenAIErrorDetails(
                            code = null,
                            message = "Unauthorized access to Fireworks AI. Please check your API key and try again.",
                            param = null,
                            type = null
                        )
                    ),
                    ClientRequestException(response, response.bodyAsText())
                )
            } else {
                try {
                    val error = response.body<OpenAIError>()
                    throw InvalidRequestException(
                        response.status.value,
                        OpenAIError(OpenAIErrorDetails(message = error.detail?.message)),
                        ClientRequestException(response, response.bodyAsText())
                    )
                } catch (_: Exception) {
                    throw InvalidRequestException(
                        response.status.value,
                        OpenAIError(OpenAIErrorDetails(message = response.bodyAsText())),
                        ClientRequestException(response, response.bodyAsText())
                    )
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    override fun getAvailableModelNames() = MODELS
    override fun dispose() = client.close()

    private fun ChatMessage.toJson(): JsonObject {
        val roleName = when (role) {
            ChatRole.System -> "user" ////todo: probably similar to Anthropic
            else -> role.role.lowercase()
        }
        return JsonObject().put("role", roleName).put("content", content)
    }
}
