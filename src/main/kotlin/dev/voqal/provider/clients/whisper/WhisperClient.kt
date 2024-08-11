package dev.voqal.provider.clients.whisper

import dev.voqal.provider.SttProvider
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.vertx.core.json.JsonObject
import java.io.File
import java.net.ConnectException

class WhisperClient(
    private val providerUrl: String
) : SttProvider {

    private val client = HttpClient {
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        try {
            val response = client.submitFormWithBinaryData(
                url = providerUrl,
                formData = formData {
                    append("audio_file", speechFile.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "audio/wav")
                        append(HttpHeaders.ContentDisposition, "filename=\"${speechFile.name}\"")
                    })
                }
            )
            if (response.status.isSuccess()) {
                val json = JsonObject(response.bodyAsText())
                return json.getString("text").trim()
            } else {
                TODO("Not yet implemented")
            }
        } catch (e: ConnectException) {
            throw ConnectException("Whisper ASR service is unavailable")
        }
    }

    override fun dispose() = client.close()
}
