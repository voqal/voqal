package dev.voqal.provider.clients.azure

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.exception.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import dev.voqal.assistant.VoqalDirective
import dev.voqal.provider.LlmProvider
import dev.voqal.provider.StmProvider
import dev.voqal.provider.clients.openai.RealtimeSession
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.VoqalContextService
import dev.voqal.services.audioCapture
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.SharedAudioCapture
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
import kotlinx.serialization.json.encodeToJsonElement

open class AzureClient(
    override val name: String,
    private val project: Project,
    private val providerUrl: String,
    private val providerKey: String,
    private val deployment: String,
    private val audioModality: Boolean = false,
    wssHeaders: Map<String, String> = emptyMap()
) : LlmProvider, StmProvider, SharedAudioCapture.AudioDataListener {

    companion object {
        const val DEFAULT_MODEL = "gpt-4o-realtime-preview"

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when {
                modelName == "Llama-3.2-90B-Vision-Instruct" -> 8192
                else -> -1
            }
        }

        @JvmStatic
        val MODELS = listOf(
            "gpt-4o-realtime-preview"
        )
    }

    private val log = project.getVoqalLogger(this::class)
    private val jsonDecoder = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(ContentNegotiation) { json(jsonDecoder) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val realtimeSession: RealtimeSession?

    init {
        realtimeSession = if (audioModality || deployment == "gpt-4o-realtime-preview") {
            project.audioCapture.registerListener(this)
            val params = buildString {
                append("?api-version=").append("2024-10-01-preview") //todo:
                append("&deployment=").append(deployment)
                append("&api-key=").append(providerKey)
            }
            var host = providerUrl.substringAfter("https://")
            if (host.endsWith("/")) {
                host = host.substringBeforeLast("/")
            }
            RealtimeSession(
                project = project,
                wssProviderUrl = "wss://$host/openai/realtime$params",
                azureHost = true,
                wssHeaders = wssHeaders
            ).apply { Disposer.register(this@AzureClient, this) }
        } else null
    }

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        if (realtimeSession != null) {
            return realtimeSession.chatCompletion(request, directive)
        }

        val requestJson = JsonObject(Json.encodeToJsonElement(request).toString())
        val tokenLimit = project.service<VoqalConfigService>().getCurrentLanguageModelSettings().tokenLimit
        if (tokenLimit != -1) {
            val reqTokenCount = project.service<VoqalContextService>().getTokenCount(requestJson.toString())
            requestJson.put("max_tokens", Math.min(4096, tokenLimit - reqTokenCount)) //todo: not hardcode 4096
        }

        val response = try {
            client.post(providerUrl) {
                header("Content-Type", "application/json")
                header("api-key", providerKey) //Azure OpenAI Connection
                header("Authorization", providerKey) //Serverless
                header("voqal-model-name", deployment)
                setBody(requestJson.encode())
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
        val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
        log.debug("Azure response status: ${response.status} in $roundTripTime ms")

        throwIfError(response)
        val completion = response.body<ChatCompletion>()
        log.debug("Completion: $completion")
        return completion
    }

    override suspend fun streamChatCompletion(
        request: ChatCompletionRequest,
        directive: VoqalDirective?
    ): Flow<ChatCompletionChunk> = flow {
        val requestJson = JsonObject(Json.encodeToJsonElement(request).toString())
            .put("stream", true).put("stream_options", JsonObject().put("include_usage", true))
        try {
            client.preparePost(providerUrl) {
                header("Content-Type", "application/json")
                header("api-key", providerKey) //Azure OpenAI Connection
                header("Authorization", providerKey) //Serverless
                header("voqal-model-name", deployment)
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
                        message = "Unauthorized access to Azure. Please check your API key and try again.",
                        param = null,
                        type = null
                    )
                ),
                ClientRequestException(response, response.bodyAsText())
            )
        }

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

    override fun getAvailableModelNames(): List<String> {
        return listOf(DEFAULT_MODEL) //todo:
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        realtimeSession?.onAudioData(data, detection)
    }

    override fun isStmProvider() = audioModality
    override fun sampleRate() = realtimeSession?.sampleRate() ?: super.sampleRate()
    override fun isLiveDataListener() = audioModality
    override fun isStreamable(): Boolean {
        //todo: support chat completion streaming in realtime api
        return !audioModality
    }

    override fun dispose() {
        project.audioCapture.removeListener(this)
        client.close()
    }
}
