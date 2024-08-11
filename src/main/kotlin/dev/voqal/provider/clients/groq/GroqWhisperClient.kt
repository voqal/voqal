package dev.voqal.provider.clients.groq

import com.aallam.openai.api.exception.OpenAITimeoutException
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.provider.SttProvider
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.Iso639Language
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

class GroqWhisperClient(
    private val project: Project,
    private val providerKey: String
) : SttProvider {

    private val providerUrl = "https://api.groq.com/openai/v1/audio/transcriptions"
    private val client = HttpClient {
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        val log = project.getVoqalLogger(this::class)
        log.info("Sending speech to TTS provider: groq")

        val durationInSeconds = getAudioDuration(speechFile)
        if (durationInSeconds < 0.1) {
            log.warn("Audio transcript file is too short. Duration: $durationInSeconds")
            return ""
        }

        var languageCode: String? = null
        val sttSettings = project.service<VoqalConfigService>().getConfig().speechToTextSettings
        if (sttSettings.language != Iso639Language.AUTO_DETECT) {
            languageCode = sttSettings.language.code
        }

        try {
            val response = client.post(providerUrl) {
                header("Authorization", "Bearer $providerKey")
                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", speechFile.readBytes(), Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=${speechFile.name}")
                        })
                        append("model", modelName)
                        append("temperature", "0")
                        append("response_format", "json")
                        if (languageCode != null) {
                            append("language", languageCode)
                        }
                    }
                ))
            }
            if (response.status.isSuccess()) {
                val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                return json["text"]?.jsonPrimitive?.content?.trim() ?: ""
            } else {
                val responseBody = response.bodyAsText()
                throw Exception("Failed to transcribe: $responseBody")
            }
        } catch (e: HttpRequestTimeoutException) {
            throw OpenAITimeoutException(e)
        }
    }

    override fun dispose() = client.close()
}
