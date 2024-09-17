package dev.voqal.provider.clients.deepgram

import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.ThreadingAssertions
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.config.settings.SpeechToTextSettings.STTProvider
import dev.voqal.provider.SttProvider
import dev.voqal.provider.TtsProvider
import dev.voqal.services.*
import dev.voqal.utils.Iso639Language
import dev.voqal.utils.SharedAudioCapture
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.LinkedBlockingQueue

class DeepgramClient(
    private val project: Project,
    private val providerKey: String,
    private val isSttProvider: Boolean = false,
    private val enableMicrophoneStreaming: Boolean = false,
    private val testMode: Boolean = false
) : SttProvider, TtsProvider, SharedAudioCapture.AudioDataListener {

    companion object {
        @JvmStatic
        val VOICES = arrayOf(
            "aura-asteria-en",
            "aura-luna-en",
            "aura-stella-en",
            "aura-athena-en",
            "aura-hera-en",
            "aura-orion-en",
            "aura-arcas-en",
            "aura-perseus-en",
            "aura-angus-en",
            "aura-orpheus-en",
            "aura-helios-en",
            "aura-zeus-en"
        )
    }

    private val log = project.getVoqalLogger(this::class)
    private val client = HttpClient {
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
        install(WebSockets)
    }
    private val httpsProviderUrl = "https://api.deepgram.com/v1"
    private val wssListenProviderUrl = "wss://api.deepgram.com/v1/listen"
    private val audioQueue = LinkedBlockingQueue<ByteArray>()
    private var disposed = false
    private lateinit var session: DefaultClientWebSocketSession
    private lateinit var readThread: Thread
    private lateinit var writeThread: Thread
    private lateinit var pingThread: Thread
    private var restartOnClose = false
    private var startListeningTime = -1L
    private var capturing = false

    init {
        if (isSttProvider) {
            project.audioCapture.registerListener(this)
            restartConnection()
        }
    }

    private fun getSttParams(): String {
        val sttSettings = project.service<VoqalConfigService>().getConfig().speechToTextSettings
        //val sendInterim = sttSettings.provider == STTProvider.DEEPGRAM //needs deepgram word timestamps
        val encoding = "linear16"
        val sampleRate = "16000"
        val modelName = "nova-2-general"
        var params = buildString {
            append("encoding=").append(encoding)
            append("&sample_rate=").append(sampleRate)
            append("&model=").append(modelName)
//            append("&smart_format=").append(true)
            append("&endpointing=").append(true)
            //append("&interim_results=").append(sendInterim)
            //append("&utterance_end_ms=").append(1000)
            if (sttSettings.language != Iso639Language.AUTO_DETECT) {
                append("&language=").append(sttSettings.language.code)
            }
        }
        if (sttSettings.provider == STTProvider.DEEPGRAM && sttSettings.queryParams.isNotEmpty()) {
            params += "&${sttSettings.queryParams}"
        }
        return params
    }

    private fun restartConnection(): Boolean {
        ThreadingAssertions.assertBackgroundThread()
        if (disposed) {
            log.warn("Deepgram client has been disposed")
            return false
        }
        log.debug("Establishing new Deepgram connection")
        if (::readThread.isInitialized) {
            readThread.interrupt()
            readThread.join()
        }
        if (::pingThread.isInitialized) {
            pingThread.interrupt()
            pingThread.join()
        }
        audioQueue.clear()

        try {
            session = runBlocking {
                //establish connection, 3 attempts
                var session: DefaultClientWebSocketSession? = null
                for (i in 0..2) {
                    try {
                        withTimeout(10_000) {
                            val params = getSttParams()
                            session = client.webSocketSession("$wssListenProviderUrl?$params") {
                                header(HttpHeaders.Authorization, "Token $providerKey")
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        if (i == 2) {
                            throw e
                        } else {
                            log.warn("Failed to connect to Deepgram. Retrying...")
                        }
                    }
                }
                session!!
            }
            startListeningTime = System.currentTimeMillis()
            log.debug("Connected to Deepgram")

            readThread = Thread(readLoop(), "DeepgramClient-Read").apply { start() }
            writeThread = Thread(writeLoop(), "DeepgramClient-Write").apply { start() }
            pingThread = Thread(pingLoop(), "DeepgramClient-Ping").apply { start() }
        } catch (e: WebSocketException) {
            if (!testMode && e.toString().contains("expected status code 101 but was 401")) {
                log.warnChat("Invalid Deepgram token")
            } else if (!testMode) {
                val warnMessage = if (e.message != null) {
                    "Deepgram connection failed: ${e.message}"
                } else {
                    "Failed to connect to Deepgram"
                }
                log.warnChat(warnMessage)
            }
            return false
        } catch (e: Exception) {
            val warnMessage = if (e.message != null) {
                "Deepgram connection failed: ${e.message}"
            } else {
                "Failed to connect to Deepgram"
            }
            log.warnChat(warnMessage)
            return false
        }
        return true
    }

    private fun readLoop(): Thread {
        return Thread {
            val configService = project.service<VoqalConfigService>()
            val fullTranscript = StringBuilder()

            try {
                while (true) {
                    val frame = runBlocking { session.incoming.receive() }
                    when (frame) {
                        is Frame.Text -> {
                            val json = JsonObject(frame.readText())
                            val channel = json.getJsonObject("channel") ?: continue
                            val alternatives = channel.getJsonArray("alternatives")
                            if (alternatives.size() != 1) {
                                log.warn("Unexpected alternatives: $alternatives")
                            }
                            val alternative = alternatives.getJsonObject(0)
                            val transcript = alternative.getString("transcript")
                            if (log.isDebugEnabled && transcript.isNotBlank()) {
                                log.debug("Deepgram response: ${json.encode().replace("\n", " ")}")
                            } else if (log.isTraceEnabled) {
                                log.trace("Deepgram response: ${json.encode().replace("\n", " ")}")
                            }
                            if (transcript.isBlank()) continue

                            val final = json.getBoolean("is_final", false)
                            if (final) {
                                log.info(buildString {
                                    append("Transcript (streaming, final")
                                    append(if (testMode) ", testMode" else "")
                                    append("): ")
                                    append(transcript)
                                })
                            } else {
                                log.debug(buildString {
                                    append("Transcript (streaming, partial")
                                    append(if (testMode) ", testMode" else "")
                                    append("): ")
                                    append(transcript)
                                })
                            }
                            if (fullTranscript.isNotEmpty() && !fullTranscript.toString().endsWith(" ")) {
                                fullTranscript.append(" ")
                            }
                            fullTranscript.append(transcript)

                            val aiProvider = configService.getAiProvider()
                            val speechId = aiProvider.asVadProvider().speechId
                            val spokenTranscript = SpokenTranscript(fullTranscript.toString(), speechId, isFinal = true)

                            runBlocking {
                                project.service<VoqalDirectiveService>().handlePartialTranscription(spokenTranscript)
                            }
                        }

                        is Frame.Close -> {
                            log.info("Connection closed")
                            break
                        }

                        else -> log.warn("Unexpected frame: $frame")
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                //Deepgram closes socket to indicate end of transcription
                log.debug("Connection closed")
            } catch (_: InterruptedException) {
            } catch (e: Exception) {
                log.error("Error processing audio: ${e.message}", e)
            } finally {
                if (restartOnClose) {
                    restartOnClose = false
                    project.scope.launch {
                        val aiProvider = configService.getAiProvider()
                        val speechId = aiProvider.asVadProvider().speechId
                        val spokenTranscript = SpokenTranscript(fullTranscript.toString(), speechId, isFinal = true)
                        project.service<VoqalDirectiveService>().handleTranscription(spokenTranscript)

                        restartConnection()
                    }
                }
            }
        }
    }

    private fun writeLoop(): Thread {
        val log = project.getVoqalLogger(this::class)
        return Thread {
            try {
                while (true) {
                    val buffer = try {
                        audioQueue.take()
                    } catch (_: InterruptedException) {
                        break
                    }

                    runBlocking {
                        session.send(Frame.Binary(true, buffer))
                    }
                }
            } catch (_: InterruptedException) {
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                log.error("Error processing audio: ${e.message}", e)
            }
        }
    }

    private fun pingLoop(): Thread {
        return Thread {
            try {
                while (true) {
                    runBlocking {
                        session.send(Frame.Text("{ \"type\": \"KeepAlive\" }"))
                        if (log.isTraceEnabled) log.trace("Sent ping")
                    }
                    Thread.sleep(5_000)
                }
            } catch (_: InterruptedException) {
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                log.error("Error processing audio: ${e.message}", e)
            }
        }
    }

    override suspend fun transcribe(speechFile: File, modelName: String): String {
        try {
            val params = getSttParams()
            val listenUrl = "$httpsProviderUrl/listen?$params"
            val response = client.post(listenUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Token $providerKey")
                    append(HttpHeaders.ContentType, "audio/wav")
                }
                setBody(speechFile.readBytes())
            }
            if (response.status.isSuccess()) {
                val json = JsonObject(response.bodyAsText())
                log.debug("Transcription: $json")

                val channels = json.getJsonObject("results").getJsonArray("channels")
                if (channels.size() != 1) {
                    log.warn("Unexpected channels: $channels")
                }
                val alternatives = channels.getJsonObject(0).getJsonArray("alternatives")
                if (alternatives.size() != 1) {
                    log.warn("Unexpected alternatives: $alternatives")
                }
                return alternatives.getJsonObject(0).getString("transcript")
            } else {
                throw ConnectException("Deepgram service is unavailable")
            }
        } catch (e: ConnectException) {
            throw ConnectException("Deepgram service is unavailable")
        }
    }

    override suspend fun speech(request: SpeechRequest): TtsProvider.RawAudio {
        try {
            val sampleRate = 24000f
            val encoding = "linear16"
            val params = "model=${request.voice!!.value}&encoding=$encoding&sample_rate=${sampleRate.toInt()}"
            val response = client.post("$httpsProviderUrl/speak?$params") {
                headers {
                    append(HttpHeaders.Authorization, "Token $providerKey")
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(JsonObject().put("text", request.input).toString())
            }
            if (response.status.isSuccess()) {
                return TtsProvider.RawAudio(
                    audio = response.bodyAsChannel(),
                    sampleRate = sampleRate,
                    bitsPerSample = 16,
                    channels = 1
                )
            } else {
                val json = JsonObject(response.bodyAsText())
                if (json.getString("err_code") == "INVALID_AUTH") {
                    throw AuthenticationException(
                        401,
                        OpenAIError(
                            OpenAIErrorDetails(
                                code = null,
                                message = json.getString("err_msg"),
                                param = null,
                                type = null
                            )
                        )
                    )
                }

                log.error("Failed to generate speech: $json")
                throw IOException("Failed to generate speech: $json")
            }
        } catch (e: ConnectException) {
            throw ConnectException("Deepgram service is unavailable. Reason: ${e.message}")
        }
    }

    override fun dispose() {
        disposed = true
        if (isSttProvider) {
            project.audioCapture.removeListener(this)
            if (::readThread.isInitialized) readThread.interrupt()
            if (::pingThread.isInitialized) pingThread.interrupt()
        }
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        if (detection.speechDetected.get()) {
            capturing = true
            detection.framesBeforeVoiceDetected.forEach {
                audioQueue.put(it.data)
            }
            audioQueue.put(data)
        } else if (capturing && !detection.speechDetected.get()) {
            capturing = false
            restartOnClose = true
            project.scope.launch {
                log.debug("No speech detected, closing stream")
                session.send(Frame.Text("{ \"type\": \"CloseStream\" }"))
            }
        }
    }

    override fun isLiveDataListener() = enableMicrophoneStreaming
    override fun isTestListener() = testMode
    override fun isWavOutput() = true
    override fun isRawOutput() = false //todo: changing to true adds a pop sound to beginning of audio???
}
