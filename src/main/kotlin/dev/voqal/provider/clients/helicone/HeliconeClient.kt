package dev.voqal.provider.clients.helicone

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIServerException
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalResponse
import dev.voqal.provider.ObservabilityProvider
import dev.voqal.services.getVoqalLogger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class HeliconeClient(
    project: Project,
    private val providerKey: String,
    private val userId: String
) : ObservabilityProvider {

    private val log = project.getVoqalLogger(this::class)
    private val client = HttpClient {
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }
    private val providerUrl = "https://api.hconeai.com/oai/v1/log"

    private suspend fun log(logData: Map<String, Any>) {
        log.debug("Sending observability data to Helicone")
        try {
            val response: HttpResponse = client.post(providerUrl) {
                header("Authorization", "Bearer $providerKey")
                header("Content-Type", "application/json")
                setBody(JsonObject(logData).toString())
            }
            val roundTripTime = response.responseTime.timestamp - response.requestTime.timestamp
            log.debug("Helicone response: ${response.status} in $roundTripTime ms")

            if (response.status != HttpStatusCode.OK) {
                log.warn("Failed to log prompt: ${response.status}")
            }
        } catch (_: CancellationException) {
            log.warn("Observability data send canceled")
        } catch (e: Exception) {
            log.warn("Error while sending data to Helicone", e)
        }
    }

    override suspend fun log(
        request: ChatCompletionRequest,
        response: VoqalResponse,
        requestTime: Long,
        responseTime: Long,
        statusCode: Int,
        cacheId: String?
    ) {
        val requestJson = JsonObject(Json.encodeToJsonElement(request).toString()).map
        var responseJson = response.backingResponse?.let { JsonObject(Json.encodeToJsonElement(it).toString()).map }
        if (responseJson == null && response.exception is OpenAIServerException) {
            responseJson = mutableMapOf<String, Any>().apply {
                val json = (response.exception.cause as ServerResponseException).response.bodyAsText()
                JsonObject(json).map.forEach {
                    put(it.key, it.value)
                }
            }
        } else if (responseJson == null && response.exception is OpenAIAPIException) {
            responseJson = mutableMapOf<String, Any>().apply {
                if (response.exception.cause is ClientRequestException) {
                    val json = (response.exception.cause as ClientRequestException).response.bodyAsText()
                    JsonObject(json).map.forEach {
                        put(it.key, it.value)
                    }
                } else {
                    val json = (response.exception.cause as IllegalStateException).message
                    JsonObject(json).map.forEach {
                        put(it.key, it.value)
                    }
                }
            }
        }
        if (responseJson == null) {
            responseJson = mutableMapOf<String, Any>().apply {
                put("error", response.exception?.stackTraceToString() ?: "An unknown error occurred")
            }
        }
        val functionName = (response.toolCalls.firstOrNull() as? ToolCall.Function)?.function?.name
        val logData = mapOf(
            "providerRequest" to mapOf(
                "url" to "N/A",
                "json" to requestJson,
                "meta" to mutableMapOf(
                    "helicone-request-id" to response.directive.requestId,
                    "helicone-user-id" to userId,
                    "helicone-property-directive-id" to response.directive.directiveId,
                    "helicone-property-directive-mode" to response.directive.assistant.directiveMode
                ).apply {
                    if (cacheId != null) {
                        put("helicone-property-cache-id", cacheId)
                    }
                    if (!response.directive.assistant.directiveMode && functionName != null) {
                        put("helicone-property-tool-name", functionName)
                    }
                }
            ),
            "providerResponse" to mapOf(
                "json" to responseJson,
                "status" to statusCode,
                "headers" to mapOf("openai-version" to "2020-10-01")
            ),
            "timing" to mapOf(
                "startTime" to mapOf(
                    "seconds" to (requestTime / 1000).toInt(),
                    "milliseconds" to (requestTime % 1000).toInt()
                ),
                "endTime" to mapOf(
                    "seconds" to (responseTime / 1000).toInt(),
                    "milliseconds" to (responseTime % 1000).toInt()
                )
            )
        )
        log(logData)
    }

    override fun dispose() = client.close()
}
