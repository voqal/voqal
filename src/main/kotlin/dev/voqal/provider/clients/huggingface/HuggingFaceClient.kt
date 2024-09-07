package dev.voqal.provider.clients.huggingface

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
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
import io.ktor.serialization.kotlinx.json.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json

class HuggingFaceClient(
    override val name: String,
    project: Project,
    private val providerKey: String,
    private val endpoint: String
) : LlmProvider {

    private val log = project.getVoqalLogger(this::class)
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val jsonObject = JsonObject()
        jsonObject.put("model", "tgi")
        val messages = JsonArray()
        request.messages.forEach {
            val message = JsonObject()
            message.put("role", it.role.role)
            message.put("content", it.content)
            messages.add(message)
        }
        jsonObject.put("messages", messages)
        jsonObject.put("stream", false)
        jsonObject.put("max_tokens", 500)
        val response = client.post(endpoint) {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $providerKey")
            setBody(jsonObject.toString())
        }
        val rawBody = response.bodyAsText()
        val json = if (rawBody.startsWith("[")) {
            JsonObject.mapFrom(JsonArray(rawBody).first()) //todo: ensure only 1
        } else {
            JsonObject(rawBody)
        }
        log.info("Body: $json")

        if (response.status.value == 200) {
            log.info("Body: $json")
            val completion = response.body<ChatCompletion>()
            return completion
        } else if (response.status.value == 401) {
            throw AuthenticationException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = json.getString("error"),
                        param = null,
                        type = null
                    )
                ),
                IllegalStateException(json.toString())
            )
        } else if (response.status.value == 429) {
            throw RateLimitException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = json.getString("error"),
                        param = null,
                        type = null
                    )
                ),
                IllegalStateException(json.toString())
            )
        } else {
            throw UnknownAPIException(
                response.status.value,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = json.getString("error", "Unknown error"),
                        param = null,
                        type = null
                    )
                ),
                IllegalStateException(json.toString())
            )
        }
    }

    override fun getAvailableModelNames() = emptyList<String>()
    override fun dispose() = client.close()
}
