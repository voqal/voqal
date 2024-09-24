package dev.voqal.provider.clients.ollama

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.api.exception.OpenAITimeoutException
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
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.util.*

class OllamaClient(
    override val name: String,
    project: Project,
    private val providerUrl: String
) : LlmProvider {

    companion object {
        @JvmStatic
        val MODELS = listOf(
            "llama3.1:latest",
            "llama3.1:8b",
            "llama3.1:instruct",
            "llama3.1:70b-instruct"
        ) //todo: get from ollama list
    }

    private val log = project.getVoqalLogger(this::class)
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(ContentNegotiation) { json(jsonDecoder) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val requestJson = toRequestJson(request).put("stream", false)

        val response = try {
            client.post(providerUrl) {
                header("Content-Type", "application/json")
                setBody(requestJson.encode())
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
        val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
        log.debug("Ollama response status: ${response.status} in $roundTripTime ms")

        throwIfError(response)
        val responseBody = response.bodyAsText()
        val choice = ChatChoice(
            index = 0,
            ChatMessage(
                ChatRole.Assistant,
                TextContent(JsonObject(responseBody).getString("response"))
            )
        )
        val completion = ChatCompletion(
            id = UUID.randomUUID().toString(),
            created = System.currentTimeMillis(),
            model = ModelId(request.model.id),
            choices = listOf(choice)
        )
        return completion
    }

    override suspend fun streamChatCompletion(
        request: ChatCompletionRequest,
        directive: VoqalDirective?
    ): Flow<ChatCompletionChunk> = flow {
        val requestJson = toRequestJson(request)

        try {
            client.preparePost(providerUrl) {
                header("Content-Type", "application/json")
                setBody(requestJson.encode())
            }.execute { response ->
                throwIfError(response)

                val channel: ByteReadChannel = response.body()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line()?.takeUnless { it.isEmpty() } ?: continue

                    val latestData = JsonObject(line)
                    emit(
                        ChatCompletionChunk(
                            id = UUID.randomUUID().toString(),
                            created = 0, //todo: System.currentTimeMillis(),
                            model = ModelId(request.model.id),
                            choices = listOf(
                                ChatChunk(
                                    index = 0,
                                    delta = ChatDelta(
                                        role = Role.Assistant,
                                        content = latestData.getString("response")
                                    )
                                )
                            ),
                            usage = Usage(0, 0, 0)
                        )
                    )
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    private fun toRequestJson(request: ChatCompletionRequest): JsonObject {
        val requestJson = JsonObject()
            .put("model", request.model.id)
            .put("prompt", request.messages.first().content) //todo: not using history
        return requestJson
    }

    private suspend fun throwIfError(response: HttpResponse) {
        if (response.status.isSuccess()) return

        val responseBody = response.bodyAsText()
        val error = JsonObject(responseBody).getString("error")
        var statusCode = 500
        if (error.contains("not found")) {
            statusCode = 404
        }
        throw InvalidRequestException(
            statusCode,
            OpenAIError(OpenAIErrorDetails(message = error)),
            IllegalStateException(responseBody)
        )
    }

    override fun isStreamable() = true
    override fun getAvailableModelNames() = MODELS
    override fun dispose() = client.close()
}
