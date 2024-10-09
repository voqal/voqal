package dev.voqal.provider.clients.openai

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.ThreadingAssertions
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowContentManager
import dev.voqal.services.*
import dev.voqal.utils.SharedAudioCapture
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.*
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.InterruptedIOException
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class RealtimeSession(
    private val project: Project,
    val wssProviderUrl: String,
    val wssHeaders: Map<String, String> = emptyMap(),
    private val azureHost: Boolean = false
) : Disposable {

    private val log = project.getVoqalLogger(this::class)
    private var capturing = false
    private lateinit var session: DefaultClientWebSocketSession
    private lateinit var readThread: Thread
    private lateinit var writeThread: Thread
    private val audioQueue = LinkedBlockingQueue<ByteArray>()
    private val jsonEncoder = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(HttpTimeout) { requestTimeoutMillis = 30_000 }
        install(WebSockets)
    }
    private val activeSession = JsonObject()
    private val readResponses = mutableSetOf<String>()
    private var playingResponseId: String? = null
    private var pis: PipedInputStream? = null
    private var pos: PipedOutputStream? = null
    private var disposed: Boolean = false
    private val responseQueue = LinkedBlockingQueue<Promise<String>>()

    init {
        restartConnection()
    }

    private fun restartConnection(): Boolean {
        ThreadingAssertions.assertBackgroundThread()
        log.debug("Establishing new Realtime API session")
        if (::readThread.isInitialized) {
            readThread.interrupt()
            readThread.join()
        }
        if (::writeThread.isInitialized) {
            writeThread.interrupt()
            writeThread.join()
        }
        audioQueue.clear()
        responseQueue.clear()

        try {
            session = runBlocking {
                //establish connection, 3 attempts
                var session: DefaultClientWebSocketSession? = null
                for (i in 0..2) {
                    try {
                        withTimeout(10_000) {
                            session = client.webSocketSession(wssProviderUrl) {
                                wssHeaders.forEach { header(it.key, it.value) }
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        if (i == 2) {
                            throw e
                        } else {
                            log.warn("Failed to connect to Realtime API. Retrying...")
                        }
                    }
                    if (session != null) break
                }
                session!!
            }
            log.debug("Connected to Realtime API")
            readThread = Thread(readLoop(), "RealtimeSession-Read").apply { start() }
            writeThread = Thread(writeLoop(), "RealtimeSession-Write").apply { start() }

            project.scope.launch {
                while (!disposed) {
                    updateSession()
                    delay(500)
                }
            }
        } catch (e: Exception) {
            val warnMessage = if (e.message != null) {
                "Realtime API connection failed: ${e.message}"
            } else {
                "Failed to connect to Realtime API"
            }
            log.warnChat(warnMessage)
            return false
        }
        return true
    }

    private fun updateSession() {
        val configService = project.service<VoqalConfigService>()
        val toolService = project.service<VoqalToolService>()
        val promptName = configService.getCurrentPromptMode()
        var nopDirective = project.service<VoqalDirectiveService>()
            .createDirective(SpokenTranscript("n/a", null), promptName = promptName)
        nopDirective = nopDirective.copy(
            assistant = nopDirective.assistant.copy(
                availableActions = toolService.getAvailableTools().values,
                includeToolsInMarkdown = false
            )
        )
        val prompt = nopDirective.toMarkdown()
        val tools = nopDirective.assistant.availableActions
            .filter { it.isVisible(nopDirective) }
            .filter {
                if (nopDirective.assistant.promptSettings!!.promptName.lowercase() == "edit mode") {
                    it.name in setOf("edit_text", "looks_good", "cancel")
                } else {
                    it.name != "answer_question"
                }
            }
            .map {
                it.asTool(nopDirective).function
            }

        val newSession = JsonObject().apply {
            put("modalities", JsonArray().add("text").add("audio"))
            put("instructions", prompt)
            put("input_audio_transcription", JsonObject().apply {
                put("model", "whisper-1")
            })
            put("tools", JsonArray(tools.map {
                JsonObject(jsonEncoder.encodeToJsonElement(it).toString())
                    .put("type", "function")
            }))
            if (azureHost) {
                put("turn_detection", JsonObject().apply {
                    put("type", "none")
                })
            } else {
                put("turn_detection", null)
            }
        }
        if (newSession.toString() == activeSession.toString()) {
            return
        } else {
            log.debug("Updating realtime session prompt")
        }
        activeSession.mergeIn(newSession)

        val json = JsonObject().apply {
            put("type", "session.update")
            put("session", activeSession)
        }
        runBlocking {
            session.send(Frame.Text(json.toString()))
        }
    }

    private fun readLoop(): Runnable {
        return Runnable {
            try {
                val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply {
                    project.scope.launch { consumeEach { it.join() } }
                }

                while (!disposed) {
                    val frame = runBlocking { session.incoming.receive() }
                    when (frame) {
                        is Frame.Text -> {
                            val json = JsonObject(frame.readText())
                            if (!json.getString("type").endsWith(".delta")) {
                                if (json.getString("type") == "error") {
                                    log.warn("Realtime error: $json")
                                } else {
                                    log.debug("Realtime event: $json")
                                }
                            }

                            if (json.getString("type") == "error") {
                                val errorMessage = json.getJsonObject("error").getString("message")
                                if (errorMessage != "Response parsing interrupted") {
                                    log.warnChat(errorMessage)
                                }
                            } else if (json.getString("type") == "response.function_call_arguments.done") {
                                val tool = json.getString("name")
                                val args = json.getString("arguments")
                                log.info("Tool call: $tool - Args: $args")

                                responseQueue.take().complete(JsonObject().apply {
                                    put("tool", tool)
                                    put("parameters", JsonObject(args))
                                }.toString())

                                project.scope.launch {
                                    session.send(Frame.Text(JsonObject().apply {
                                        put("type", "conversation.item.create")
                                        put("item", JsonObject().apply {
                                            put("type", "function_call_output")
                                            put("status", "completed")
                                            put("call_id", json.getString("call_id"))
                                            put("output", "success")
                                        })
                                    }.toString()))
                                }
                            } else if (json.getString("type") == "response.audio.delta") {
                                channel.trySend(project.scope.launch(start = CoroutineStart.LAZY) {
                                    if (pis == null) {
                                        val responseId = json.getString("response_id")
                                        if (readResponses.contains(responseId)) {
                                            return@launch
                                        } else {
                                            log.info("Playing audio response: $responseId")
                                            readResponses.add(responseId)
                                            playingResponseId = responseId
                                        }
                                        pis = PipedInputStream()
                                        pos = PipedOutputStream()
                                        pos!!.connect(pis!!)

                                        project.scope.launch {
                                            project.service<VoqalVoiceService>().playStreamingWavFile(pis!!)
                                            pis = null
                                            pos = null
                                            playingResponseId = null
                                        }
                                    }
                                    if (json.getString("response_id") == playingResponseId) {
                                        try {
                                            pos?.write(Base64.getDecoder().decode(json.getString("delta")))
                                        } catch (ignore: Throwable) {
                                        }
                                    }
                                })
                            } else if (json.getString("type") == "response.audio.done") {
                                if (json.getString("response_id") == playingResponseId) {
                                    channel.trySend(project.scope.launch(start = CoroutineStart.LAZY) {
                                        try {
                                            pos?.write(SharedAudioCapture.EMPTY_BUFFER)
                                        } catch (ignore: Throwable) {
                                        }
                                    })
                                }
                            } else if (json.getString("type") == "input_audio_buffer.speech_started") {
                                log.info("Realtime speech started")
                                stopCurrentVoice()
                            } else if (json.getString("type") == "input_audio_buffer.speech_stopped") {
                                log.info("Realtime speech stopped")
                            } else if (json.getString("type") == "conversation.item.input_audio_transcription.completed") {
                                val transcript = json.getString("transcript")
                                log.info("User transcript: $transcript")
                                val chatContentManager = project.service<ChatToolWindowContentManager>()
                                chatContentManager.addUserMessage(transcript)
                            } else if (json.getString("type") == "response.audio_transcript.done") {
                                val transcript = json.getString("transcript")
                                log.info("Assistant transcript: $transcript")

                                responseQueue.take().complete(JsonObject().apply {
                                    put("tool", "answer_question")
                                    put("parameters", JsonObject().apply {
                                        put("answer", transcript)
                                        put("audioModality", true)
                                    })
                                }.toString())
                            } else if (json.getString("type") == "response.text.done") {
                                val text = json.getString("text")
                                log.info("Assistant text: $text")

                                responseQueue.take().complete(JsonObject().apply {
                                    put("tool", "answer_question")
                                    put("parameters", JsonObject().apply {
                                        put("answer", text)
                                        //put("audioModality", true)
                                    })
                                }.toString())
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
                //todo: Deepgram closes socket to indicate end of transcription
                log.debug("Connection closed")
            } catch (_: InterruptedException) {
            } catch (_: InterruptedIOException) {
            } catch (e: Exception) {
                log.error("Error processing audio: ${e.message}", e)
            } finally {
                if (!disposed) {
                    project.scope.launch {
                        restartConnection()
                    }
                }
            }
        }
    }

    private fun stopCurrentVoice() {
        playingResponseId = null
        try {
            pos?.close()
            pis?.close()
        } catch (ignore: Throwable) {
        }
    }

    private fun writeLoop(): Runnable {
        val log = project.getVoqalLogger(this::class)
        return Runnable {
            try {
                while (!disposed) {
                    val buffer = try {
                        audioQueue.take()
                    } catch (_: InterruptedException) {
                        break
                    }

                    if (buffer === SharedAudioCapture.EMPTY_BUFFER) {
                        log.debug("No speech detected, flushing stream")
                        runBlocking {
                            session.send(Frame.Text(JsonObject().apply {
                                put("type", "input_audio_buffer.commit")
                            }.toString()))
                            session.send(Frame.Text(JsonObject().apply {
                                put("type", "response.create")
                            }.toString()))
                        }
                    } else {
                        runBlocking {
                            val json = JsonObject().apply {
                                put("type", "input_audio_buffer.append")
                                put("audio", Base64.getEncoder().encodeToString(buffer))
                            }
                            session.send(Frame.Text(json.toString()))
                        }
                    }
                }
            } catch (_: InterruptedException) {
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                log.error("Error processing audio: ${e.message}", e)
            }
        }
    }

    fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        if (detection.speechDetected.get()) {
            if (playingResponseId != null) {
                log.warn("Sending response cancel")
                stopCurrentVoice()
                runBlocking {
                    session.send(Frame.Text(JsonObject().apply {
                        put("type", "response.cancel")
                    }.toString()))
                }
            }

            capturing = true
            detection.framesBeforeVoiceDetected.forEach {
                audioQueue.put(it.data)
            }
            audioQueue.put(data)
        } else if (capturing && !detection.speechDetected.get()) {
            capturing = false
            audioQueue.put(SharedAudioCapture.EMPTY_BUFFER)
        }
    }

    suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val eventId = "voqal." + UUID.randomUUID().toString()
        val json = JsonObject().apply {
            put("event_id", "$eventId.conversation.item")
            put("type", "conversation.item.create")
            put("item", JsonObject().apply {
                put("type", "message")
                put("status", "completed")
                put("role", "user")
                put("content", JsonArray().add(JsonObject().apply {
                    put("type", "input_text")
                    put("text", directive!!.developer.transcription)
                }))
            })
        }
        session.send(Frame.Text(json.toString()))

        val promise = Promise.promise<String>()
        responseQueue.add(promise)

        session.send(Frame.Text(JsonObject().apply {
            put("event_id", "$eventId.response") //todo: doesn't correlate with response
            put("type", "response.create")
        }.toString()))

        //todo: realtime can choose to merge reqs (i.e. hi 3 times quickly = 1 response)
        val responseJson = promise.future().coAwait()
        return ChatCompletion(
            id = "n/a",
            created = System.currentTimeMillis(),
            model = ModelId("n/a"),
            choices = listOf(
                ChatChoice(
                    index = 0,
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(
                            content = responseJson
                        )
                    )
                )
            )
        )
    }

    fun sampleRate() = 24000f

    override fun dispose() {
        disposed = true
        if (::session.isInitialized) {
            runBlocking { session.close(CloseReason(CloseReason.Codes.NORMAL, "Disposed")) }
        }
        if (::writeThread.isInitialized) writeThread.interrupt()
    }
}
