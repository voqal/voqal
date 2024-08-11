package dev.voqal.provider.clients.googleapi

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.model.ModelId
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.memory.local.asDirectiveTool
import dev.voqal.provider.AiProvider
import dev.voqal.provider.LlmProvider
import dev.voqal.provider.StmProvider
import dev.voqal.provider.clients.picovoice.NativesExtractor
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class GoogleApiClient(
    override val name: String,
    private val project: Project,
    private val providerKey: String
) : AiProvider, StmProvider, LlmProvider {

    companion object {
        const val DEFAULT_MODEL = "gemini-1.5-flash-latest"

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when (modelName) {
                "gemini-1.5-flash-latest" -> 1048576
                "gemini-1.5-pro-latest" -> 2097152
                else -> -1
            }
        }

        @JvmStatic
        val MODELS = listOf(
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro-latest"
        )
    }

    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val log = project.getVoqalLogger(this::class)
        val modelName = request.model.id
        val providerUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$providerKey"
        try {
            val requestJson = JsonObject()
                .put("contents", JsonArray(request.messages.map { it.toJson() }))
            if (directive?.internal?.includeToolsInMarkdown == false) {
                requestJson.put("tools", JsonArray().apply {
                    val toolsArray = JsonArray(request.tools?.map { it.asDirectiveTool() }
                        ?.map { JsonObject(Json.encodeToString(it)) })
                    val functionDeclarations = toolsArray.map {
                        val jsonObject = it as JsonObject
                        jsonObject.remove("type")
                        jsonObject.getJsonObject("function")
                    }
                    add(JsonObject().put("function_declarations", JsonArray(functionDeclarations)))
                })

                requestJson.put(
                    "tool_config",
                    JsonObject().put("function_calling_config", JsonObject().put("mode", "ANY"))
                )
            }

            if (directive?.internal?.speechId != null && directive.internal.usingAudioModality) {
                log.debug("Using audio modality")
                val speechId = directive.internal.speechId
                val speechDirectory = File(NativesExtractor.workingDirectory, "speech")
                speechDirectory.mkdirs()
                val speechFile = File(speechDirectory, "developer-$speechId.wav")
                val audio1Bytes = speechFile.readBytes()

                //add audio bytes to last contents/parts
                val lastContent = requestJson.getJsonArray("contents")
                    .getJsonObject(requestJson.getJsonArray("contents").size() - 1)
                val parts = lastContent.getJsonArray("parts")
                parts.add(JsonObject().put("inline_data", JsonObject().apply {
                    put("mime_type", "audio/wav")
                    put("data", Base64.getEncoder().encodeToString(audio1Bytes))
                }))
                lastContent.put("parts", parts)
            }

            val response = client.post(providerUrl) {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                setBody(requestJson.encode())
            }
            val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
            log.debug("Google API response status: ${response.status} in $roundTripTime ms")

            if (response.status.isSuccess()) { //todo: better
                val json = JsonObject(response.bodyAsText())
                log.debug("Completion: $json")
                val candidates = json.getJsonArray("candidates")
                val bestContent = candidates.getJsonObject(0).getJsonObject("content")
                val bestContentParts = bestContent?.getJsonArray("parts")
                //todo: null is temp fix
                val textResponse = bestContentParts?.getJsonObject(0)?.getString("text") ?: "null"
                val choice = ChatChoice(
                    index = 0,
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(textResponse)
                    )
                )
                val completion = ChatCompletion(
                    id = UUID.randomUUID().toString(),
                    created = System.currentTimeMillis(),
                    model = ModelId(request.model.id),
                    choices = listOf(choice),
                    usage = Usage(
                        promptTokens = json.getJsonObject("usageMetadata").getInteger("promptTokenCount"),
                        completionTokens = json.getJsonObject("usageMetadata").getInteger("candidatesTokenCount"),
                        totalTokens = json.getJsonObject("usageMetadata").getInteger("totalTokenCount")
                    )
                )
                return completion
            } else if (response.status.value == 401) {
                throw AuthenticationException(
                    response.status.value,
                    OpenAIError(
                        OpenAIErrorDetails(
                            code = null,
                            message = "Unauthorized access to Google API. Please check your API key and try again.",
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
                throw InvalidRequestException(
                    response.status.value,
                    OpenAIError(
                        OpenAIErrorDetails(
                            code = null,
                            message = "Invalid request to Google API. Please check your configuration and try again.",
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
    override fun dispose() = Unit

    private fun ChatMessage.toJson(): JsonObject {
        return JsonObject().apply {
            put("role", if (role == Role.Assistant) "model" else "user")
            put("parts", JsonArray().apply {
                add(JsonObject().put("text", content))
            })
        }
    }
}
