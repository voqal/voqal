package dev.voqal.provider.clients.ollama

import com.aallam.openai.api.chat.*
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
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json
import java.util.*

class OllamaClient(
    override val name: String,
    private val project: Project,
    private val providerUrl: String
) : LlmProvider {

    companion object {
        @JvmStatic
        val MODELS = listOf(
            "llama3:latest",
            "llama3:8b",
            "llama3:instruct",
            "llama3:70b-instruct"
        )
    }

    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val log = project.getVoqalLogger(this::class)
        try {
            val startTime = System.currentTimeMillis()
            val jsonObject = JsonObject()
                .put("model", request.model.id)
                .put("prompt", request.messages.first().content) //todo: not using history
            val fullResponse = StringBuilder()
            val infillSource = StringBuilder()
            var status = 200 //todo: this
            client.preparePost(providerUrl) {
                header("Content-Type", "application/json")
                setBody(jsonObject.toString())
            }.execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line()?.takeUnless { it.isEmpty() } ?: continue
                    val json = JsonObject(line)
                    fullResponse.append(json)
                    val responseCode = json.getString("response")
                    infillSource.append(responseCode)
                }
            }
            val roundTripTime = System.currentTimeMillis() - startTime
            log.debug("Ollama response status: $status in $roundTripTime ms")

            if (status == 200) {
                val error = try {
                    JsonObject(fullResponse.toString()).getString("error")
                } catch (_: Exception) {
                    null
                }
                if (error != null) {
                    var statusCode = 500
                    if (error.contains("not found")) {
                        statusCode = 404
                    }
                    throw InvalidRequestException(
                        statusCode,
                        OpenAIError(OpenAIErrorDetails(message = error)),
                        IllegalStateException(fullResponse.toString())
                    )
                }

                val choice = ChatChoice(
                    index = 0,
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(infillSource.toString())
                    )
                )
                val completion = ChatCompletion(
                    id = UUID.randomUUID().toString(),
                    created = System.currentTimeMillis(),
                    model = ModelId(request.model.id),
                    choices = listOf(choice)
                )
                return completion
            } else {
                log.error("Ollama completion failed: $status")
                throw Exception("Ollama completion failed: $status")
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    override fun getAvailableModelNames(): List<String> = MODELS
    override fun dispose() = client.close()

    private fun ChatMessage.toJson(): JsonObject {
        return JsonObject().put("role", "user").put("content", content)
    }
}
