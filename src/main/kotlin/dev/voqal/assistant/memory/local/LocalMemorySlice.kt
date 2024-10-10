package dev.voqal.assistant.memory.local

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.model.ModelId
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.processing.ResponseParser
import dev.voqal.assistant.tool.text.EditTextTool
import dev.voqal.config.settings.PromptSettings.FunctionCalling
import dev.voqal.services.*
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.util.*

/**
 * Holds chat messages with the LLM in local memory.
 */
class LocalMemorySlice(
    private val project: Project
) : MemorySlice {

    override val id = UUID.randomUUID().toString()

    @VisibleForTesting
    val messageList = mutableListOf<ChatMessage>()

    private val log = project.getVoqalLogger(this::class)

    override suspend fun addMessage(
        directive: VoqalDirective,
        addMessage: Boolean
    ): VoqalResponse {
        val promptSettings = directive.assistant.promptSettings
            ?: throw IllegalStateException("Prompt settings not found")
        val configService = project.service<VoqalConfigService>()
        val config = configService.getConfig()
        if (!config.pluginSettings.enabled) {
            throw IllegalStateException("Plugin is disabled")
        }

        var lmSettings = directive.getLanguageModelSettings()
        if (promptSettings.languageModel.isNotBlank()) {
            val settings = config.languageModelsSettings.models.firstOrNull {
                it.name == promptSettings.languageModel
            }
            if (settings != null) {
                lmSettings = settings
            }
        }

        val systemPrompt = directive.toMarkdown()
        if (log.isTraceEnabled) log.trace("${promptSettings.promptName}: $systemPrompt")

        if (promptSettings.promptName == "Edit Mode") {
            project.service<VoqalStatusService>().updateText("Querying AI provider: ${lmSettings.name}")
        }
        var includeToolsInMarkdown = promptSettings.functionCalling == FunctionCalling.MARKDOWN
        if (promptSettings.promptName == "Edit Mode") {
            includeToolsInMarkdown = true //todo: edit mode doesn't support function calls
        }
        val request = if (messageList.isEmpty()) {
            if (addMessage) {
                messageList.add(ChatMessage(ChatRole.System, systemPrompt))
            } else {
                log.debug("No message added")
            }
            if (includeToolsInMarkdown) {
                ChatCompletionRequest(
                    model = ModelId(lmSettings.modelName),
                    messages = getMessages(),
                    responseFormat = ChatResponseFormat.Text,
                    seed = lmSettings.seed,
                    temperature = lmSettings.temperature
                )
            } else {
                ChatCompletionRequest(
                    model = ModelId(lmSettings.modelName),
                    messages = getMessages(),
                    tools = directive.assistant.availableActions
                        .filter { it.isVisible(directive) }
                        .map {
                            if (directive.assistant.directiveMode && !it.supportsDirectiveMode()) {
                                it.asTool(directive).asDirectiveTool()
                            } else {
                                it.asTool(directive)
                            }
                        },
                    //responseFormat = ChatResponseFormat.JsonObject, //todo: config JsonFormat, non-markdown tools
                    seed = lmSettings.seed,
                    temperature = lmSettings.temperature
                )
            }
        } else {
            if (addMessage) {
                messageList.add(ChatMessage(ChatRole.User, directive.developer.transcription))
            } else {
                log.debug("No message added")
            }
            if (includeToolsInMarkdown) {
                ChatCompletionRequest(
                    model = ModelId(lmSettings.modelName),
                    messages = getMessages(),
                    responseFormat = ChatResponseFormat.Text,
                    seed = lmSettings.seed,
                    temperature = lmSettings.temperature
                )
            } else {
                ChatCompletionRequest(
                    model = ModelId(lmSettings.modelName),
                    messages = getMessages(),
                    tools = directive.assistant.availableActions
                        .filter { it.isVisible(directive) }
                        .map { it.asTool(directive) },
                    //responseFormat = ChatResponseFormat.JsonObject, //todo: config JsonFormat, non-markdown tools
                    seed = lmSettings.seed,
                    temperature = lmSettings.temperature
                )
            }
        }
        if (log.isTraceEnabled) log.trace("Chat completion request: ${request.messages.last()}")

        val aiProvider = configService.getAiProvider()
        val requestTime = System.currentTimeMillis()
        var completion: ChatCompletion? = null
        try {
            val llmProvider = aiProvider.asLlmProvider(lmSettings.name)
            if (promptSettings.promptName == "Edit Mode" && promptSettings.streamCompletions && llmProvider.isStreamable()) {
                val chunks = mutableListOf<ChatCompletionChunk>()
                var deltaRole: Role? = null
                val fullText = StringBuilder()

                val chunkProcessingChannel = Channel<ChatCompletionChunk>(capacity = Channel.UNLIMITED)
                val processingJob = CoroutineScope(Dispatchers.Default).launch {
                    for (chunk in chunkProcessingChannel) {
                        try {
                            val updatedChunk = chunk.copy(
                                choices = chunk.choices.map {
                                    it.copy(
                                        delta = it.delta!!.copy(
                                            role = deltaRole,
                                            content = fullText.toString()
                                        )
                                    )
                                }
                            )
                            val response = ResponseParser.parseEditMode(updatedChunk, directive)
                            val argsString = (response.toolCalls.first() as ToolCall.Function).function.arguments
                            project.service<VoqalToolService>().blindExecute(
                                tool = EditTextTool(),
                                args = JsonObject(argsString).put("streaming", true),
                                memoryId = directive.assistant.memorySlice.id
                            )
                        } catch (e: Throwable) {
                            continue
                        }
                    }
                }

                llmProvider.streamChatCompletion(request, directive).collect {
                    chunks.add(it)
                    if (deltaRole == null) {
                        deltaRole = it.choices[0].delta?.role
                    }
                    fullText.append(it.choices.firstOrNull()?.delta?.content ?: "")
                    if (it.choices.firstOrNull()?.delta?.content?.contains("\n") in setOf(null, false)) {
                        return@collect //wait till new line to progress streaming edit
                    }

                    chunkProcessingChannel.send(it)
                }

                chunkProcessingChannel.close()
                processingJob.join()

                completion = toChatCompletion(chunks, fullText.toString())
            } else {
                completion = llmProvider.chatCompletion(request, directive)
            }
            val responseTime = System.currentTimeMillis()

            //todo: check other choices
            val messageContent = completion.choices.firstOrNull()?.message?.messageContent
            val textContent = if (messageContent is TextContent) {
                messageContent.content
            } else {
                messageContent.toString()
            }
            messageList.add(ChatMessage(ChatRole.Assistant, textContent))

            val response = when (promptSettings.promptName) {
                "Edit Mode" -> ResponseParser.parseEditMode(completion, directive)
                else -> ResponseParser.parse(completion, directive)
            }
            if (aiProvider.isObservabilityProvider()) {
                log.debug("Logging successful observability data")
                val op = aiProvider.asObservabilityProvider()
                if (ApplicationManager.getApplication().isUnitTestMode) {
                    op.log(request, response, requestTime, responseTime)
                } else {
                    op.asyncLog(project, request, response, requestTime, responseTime)
                }
            }

            //todo: TPS should be a part of observability
            val tokenCount = project.service<VoqalContextService>().getTokenCount(textContent)
            val elapsedTimeMs = responseTime - requestTime
            val elapsedTimeSeconds = elapsedTimeMs / 1000.0
            val tps = if (elapsedTimeSeconds > 0) tokenCount / elapsedTimeSeconds else 0.0
            log.debug("Response TPS: $tps")

            return response
        } catch (e: Throwable) {
            val responseTime = System.currentTimeMillis()
            if (aiProvider.isObservabilityProvider()) {
                log.debug("Logging failure observability data")
                val response = VoqalResponse(directive, emptyList(), completion, e)
                val op = aiProvider.asObservabilityProvider()
                val statusCode = (e as? OpenAIAPIException)?.statusCode ?: 500
                if (ApplicationManager.getApplication().isUnitTestMode) {
                    op.log(request, response, requestTime, responseTime, statusCode)
                } else {
                    op.asyncLog(project, request, response, requestTime, responseTime, statusCode)
                }
            }
            throw e
        }
    }

    private fun getMessages(): List<ChatMessage> {
        return messageList.map {
            if (it.role == ChatRole.Assistant) {
                if (it.messageContent == null) {
                    it.copy(
                        messageContent = TextContent(it.toolCalls.toString()),
                        toolCalls = null
                    )
                } else {
                    it
                }
            } else {
                it
            }
        }
    }

    private fun toChatCompletion(chunks: List<ChatCompletionChunk>, fullText: String): ChatCompletion {
        val chunk = chunks.last()
        val role = chunks.first().choices.first().delta!!.role!!
        return ChatCompletion(
            id = chunk.id,
            created = chunk.created.toLong(),
            model = chunk.model,
            choices = listOf(
                ChatChoice(
                    index = 0,
                    message = ChatMessage(
                        role = role,
                        messageContent = TextContent(fullText)
                    )
                )
            ),
            usage = chunk.usage,
            systemFingerprint = chunk.systemFingerprint
        )
    }
}

fun Tool.asDirectiveTool(): Tool {
    return copy(
        function = function.copy(
            parameters = Parameters.fromJsonString(
                JsonObject().apply {
                    put("type", "object")
                    put("properties", JsonObject().apply {
                        put("directive", JsonObject().apply {
                            put("type", "string")
                            put("description", "The directive to pass to the tool")
                        })
                    })
                }.toString()
            )
        )
    )
}
