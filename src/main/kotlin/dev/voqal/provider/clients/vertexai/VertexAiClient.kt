package dev.voqal.provider.clients.vertexai

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.api.model.ModelId
import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.InvalidArgumentException
import com.google.api.gax.rpc.ResourceExhaustedException
import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.GenerationConfig
import com.google.cloud.vertexai.generativeai.ChatSession
import com.google.cloud.vertexai.generativeai.ContentMaker
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.PartMaker
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.provider.LlmProvider
import dev.voqal.provider.StmProvider
import dev.voqal.provider.clients.picovoice.NativesExtractor
import dev.voqal.services.getVoqalLogger
import java.io.File
import java.io.FileInputStream

class VertexAiClient(
    override val name: String,
    private val project: Project,
    projectId: String,
    location: String
) : LlmProvider, StmProvider {

    companion object {
        const val DEFAULT_MODEL = "gemini-1.5-flash-preview-0514"

        @JvmStatic
        fun getTokenLimit(modelName: String): Int {
            return when (modelName) {
                "gemini-1.5-flash-preview-0514" -> 1048576
                else -> -1
            }
        }

        @JvmStatic
        val MODELS = listOf(
            "gemini-1.5-flash-preview-0514"
        )
    }

    private val chatMap = mutableMapOf<String, ChatSession>() //todo: disposals
    private val vertexAi = VertexAI.Builder()
        .setProjectId(projectId)
        .setLocation(location)
        .build()

    override suspend fun chatCompletion(request: ChatCompletionRequest, directive: VoqalDirective?): ChatCompletion {
        val log = project.getVoqalLogger(this::class)
        val generationConfig = GenerationConfig.newBuilder()
            .setMaxOutputTokens(8192)
            .setTemperature(1f)
            .setTopP(0.95f) //todo: these
            .build()

        val chatSession = if (chatMap[directive!!.assistant.memorySlice.id] != null) {
            log.debug("Using existing chat session")
            chatMap[directive.assistant.memorySlice.id]!!
        } else {
            log.debug("Creating new chat session")
            val systemInstruction = ContentMaker.fromMultiModalData(request.messages.first().content)
            val model = GenerativeModel.Builder()
                .setModelName(request.model.id)
                .setVertexAi(vertexAi)
                .setGenerationConfig(generationConfig)
                .setSystemInstruction(systemInstruction)
                .build()
            val chatSession = model.startChat()
            chatMap[directive.assistant.memorySlice.id] = chatSession
            chatSession
        }

        //todo: need to check response messages for the error messages voqal adds and add to this chat

        val content = if (directive.assistant.speechId != null && directive.assistant.usingAudioModality) {
            val speechId = directive.assistant.speechId
            val speechDirectory = File(NativesExtractor.workingDirectory, "speech")
            speechDirectory.mkdirs()
            val speechFile = File(speechDirectory, "developer-$speechId.wav")
            val audio1Bytes = ByteArray(speechFile.length().toInt())
            FileInputStream(speechFile).use { audio1FileInputStream ->
                audio1FileInputStream.read(audio1Bytes)
            }
            val audio1 = PartMaker.fromMimeTypeAndData("audio/wav", audio1Bytes)
            log.debug("Attached ${audio1Bytes.size} bytes of audio to the request")


            ContentMaker.fromMultiModalData("Developer transcription attached", audio1)
        } else {
            log.debug("Using textual transcription")
            ContentMaker.fromMultiModalData(directive.developer.transcription)
        }
        val resp = try {
            chatSession.sendMessage(content)
        } catch (e: ApiException) {
            if (e is ResourceExhaustedException) {
                throw RateLimitException(
                    429,
                    OpenAIError(
                        OpenAIErrorDetails(
                            code = null,
                            message = e.message,
                            param = null,
                            type = null
                        )
                    ),
                    IllegalStateException("{}")
                )
            } else if (e is InvalidArgumentException) {
                throw InvalidRequestException(
                    400,
                    OpenAIError(
                        OpenAIErrorDetails(
                            code = null,
                            message = e.message,
                            param = null,
                            type = null
                        )
                    ),
                    IllegalStateException("{}")
                )
            }
            throw e
        } catch (e: IllegalStateException) {
            throw InvalidRequestException(
                400,
                OpenAIError(
                    OpenAIErrorDetails(
                        code = null,
                        message = e.message,
                        param = null,
                        type = null
                    )
                ),
                IllegalStateException("{}")
            )
        }
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
                            content = resp.getCandidates(0).content.getParts(0).text
                        )
                    )
                )
            )
        )
    }

    override fun getAvailableModelNames() = MODELS
    override fun dispose() = vertexAi.close()
}
