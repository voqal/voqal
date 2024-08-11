package dev.voqal.provider.clients.assemblyai

import com.intellij.openapi.project.Project
import dev.voqal.provider.SttProvider
import dev.voqal.services.getVoqalLogger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File

class AssemblyAiClient(
    private val project: Project,
    private val providerKey: String
) : SttProvider {

    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    private suspend fun upload(request: ByteArray): String {
        try {
            val response: HttpResponse = client.post("https://api.assemblyai.com/v2/upload") {
                header("Authorization", "Bearer $providerKey")
                header("Content-Type", "application/octet-stream")
                setBody(request)
            }
            val bodyAsJsonObject = JsonObject(response.bodyAsText())
            return bodyAsJsonObject.getString("upload_url")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        val log = project.getVoqalLogger(this::class)
        log.debug("Uploading audio: $speechFile")
        val fileUrl = upload(speechFile.readBytes())
        log.debug("Audio uploaded to: $fileUrl")

        log.debug("Creating transcript job")
        val url = "https://api.assemblyai.com/v2/transcript"
        val response: HttpResponse = client.post(url) {
            header("Authorization", providerKey)
            header("Content-Type", "application/json")
            setBody(JsonObject().apply {
                put("audio_url", fileUrl)
            }.toString())
        }
        val bodyAsJsonObject = JsonObject(response.bodyAsText())
        val transcriptId = bodyAsJsonObject.getString("id")
        log.debug("Transcript job id: $transcriptId")

        var getResponse = client.get("$url/$transcriptId") {
            header("Authorization", providerKey)
            header("Content-Type", "application/json")
        }
        while (getResponse.status.value == 200) {
            val getBodyAsJsonObject = JsonObject(getResponse.bodyAsText())
            val status = getBodyAsJsonObject.getString("status")
            log.debug("Transcript status: $status")
            if (status == "processing") {
                delay(1000)
                getResponse = client.get("$url/$transcriptId") {
                    header("Authorization", providerKey)
                    header("Content-Type", "application/json")
                }
            } else {
                break
            }
        }
        val getBodyAsJsonObject = JsonObject(getResponse.bodyAsText())
        val finalTranscript = getBodyAsJsonObject.getString("text")
        log.debug("Final transcript: $finalTranscript")
        return finalTranscript
    }

    override fun dispose() = client.close()
}
