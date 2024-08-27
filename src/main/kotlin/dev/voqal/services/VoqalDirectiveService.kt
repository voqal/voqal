package dev.voqal.services

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.exception.OpenAITimeoutException
import com.aallam.openai.api.exception.RateLimitException
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.InternalContext
import dev.voqal.assistant.context.code.SelectedCode
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.flaw.error.parse.ResponseParseError
import dev.voqal.assistant.focus.DirectiveExecution
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.memory.local.LocalMemorySlice
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.AnswerQuestionTool
import dev.voqal.assistant.tool.text.EditTextTool
import dev.voqal.config.settings.TextToSpeechSettings
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowContentManager
import dev.voqal.status.VoqalStatus.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.pow

/**
 * Handles developer transcriptions, transforming them into Voqal directives, executing them, and handling the
 * resulting responses.
 */
@Service(Service.Level.PROJECT)
class VoqalDirectiveService(private val project: Project) {

    companion object {
        fun convertJsonElementToMap(jsonElement: JsonElement): Any? = when (jsonElement) {
            is JsonObject -> jsonElement.mapValues { convertJsonElementToMap(it.value) }
            is JsonArray -> jsonElement.map { convertJsonElementToMap(it) }
            is JsonPrimitive -> when {
                jsonElement.isString -> jsonElement.content
                jsonElement.booleanOrNull != null -> jsonElement.boolean
                jsonElement.intOrNull != null -> jsonElement.int
                jsonElement.longOrNull != null -> jsonElement.long
                jsonElement.doubleOrNull != null -> jsonElement.double
                jsonElement.floatOrNull != null -> jsonElement.float
                else -> jsonElement.contentOrNull
            }

            else -> jsonElement
        }
    }

    private val activeDirectives = mutableMapOf<VoqalDirective, DirectiveExecution>()
    private val executionHistory = mutableListOf<DirectiveExecution>()
    private val listeners: MutableList<(VoqalDirectiveService, DirectiveExecution) -> Unit> = CopyOnWriteArrayList()

    init {
        val log = project.getVoqalLogger(this::class)
        project.scope.launch {
            while (!project.isDisposed) {
                if (activeDirectives.isNotEmpty()) {
                    val activeDirectives = activeDirectives.values.toList()
                    activeDirectives.forEach {
                        if (it.endTime == null) {
                            val duration = System.currentTimeMillis() - it.startTime
                            if (duration > 60_000) {
                                log.warn("Directive ${it.directive.directiveId} has been running for $duration ms")
                            }
                        }
                    }
                }
                delay(30_000)
            }
        }
    }

    /**
     * Send the partial developer transcription to the appropriate Voqal mode for processing.
     */
    suspend fun handlePartialTranscription(spokenTranscript: SpokenTranscript) {
        val promptSettings = project.service<VoqalConfigService>().getCurrentPromptSettings()
        if (promptSettings.showPartialResults && project.service<VoqalStatusService>().getStatus() == EDITING) {
            project.service<VoqalToolService>().blindExecute(
                EditTextTool(), io.vertx.core.json.JsonObject()
                    .put("spokenTranscript", spokenTranscript.toJson())
            )
        }
    }

    /**
     * Send the finalized developer transcription to the appropriate Voqal mode for processing.
     */
    suspend fun handleTranscription(
        spokenTranscript: SpokenTranscript,
        textOnly: Boolean = false,
        chatMessage: Boolean = false,
        usingAudioModality: Boolean = false
    ) {
        val log = project.getVoqalLogger(this::class)
        if (spokenTranscript.transcript.isBlank()) {
            log.debug("Ignoring empty transcription")
            return
        }

        //add to chat window
        project.service<ChatToolWindowContentManager>()
            .addUserMessage(spokenTranscript.transcript, spokenTranscript.speechId)

        //final intent check
        val detectedIntent = project.service<VoqalToolService>().intentCheck(spokenTranscript)
        if (detectedIntent != null) {
            log.debug("Blind executing detected intent: ${spokenTranscript.transcript}")
            project.service<VoqalToolService>().blindExecute(
                detectedIntent.executable, io.vertx.core.json.JsonObject.mapFrom(detectedIntent.args), chatMessage
            )
            return
        }

        val aiProvider = project.service<VoqalConfigService>().getAiProvider()
        if (!aiProvider.isLlmProvider()) {
            log.warn("No language model provider found")
            project.service<VoqalStatusService>().updateText("No language model provider found")
            return
        }

        //send transcription to LLM
        val currentStatus = project.service<VoqalStatusService>().getStatus()
        if (currentStatus == IDLE) {
            val directive = asDirective(
                transcription = spokenTranscript,
                textOnly = textOnly,
                usingAudioModality = usingAudioModality,
                chatMessage = chatMessage,
                promptName = "Idle Mode"
            )
            executeDirective(directive)
        } else if (currentStatus == EDITING) {
            val directive = asDirective(
                transcription = spokenTranscript,
                textOnly = textOnly,
                usingAudioModality = usingAudioModality,
                chatMessage = chatMessage,
                promptName = "Edit Mode"
            )
            executeDirective(directive)
        } else if (currentStatus == SEARCHING) {
            val directive = asDirective(
                transcription = spokenTranscript,
                textOnly = textOnly,
                usingAudioModality = usingAudioModality,
                chatMessage = chatMessage,
                promptName = "Search Mode"
            )
            executeDirective(directive)
        } else {
            log.warn("Unhandled status: $currentStatus")
        }
    }

    fun reset() {
        activeDirectives.clear()
        executionHistory.clear()
    }

    fun onDirectiveExecution(
        disposable: Disposable = project.service<ProjectScopedService>(),
        listener: (VoqalDirectiveService, DirectiveExecution) -> Unit
    ) {
        val log = project.getVoqalLogger(this::class)
        listeners.add(listener)
        log.trace("Added directive execution listener. Available directive execution listeners: " + listeners.size)

        Disposer.register(disposable) {
            listeners.remove(listener)
            log.trace("Removed directive execution listener. Available directive execution listeners: " + listeners.size)
        }
    }

    fun isActive(): Boolean {
        return activeDirectives.isNotEmpty()
    }

    fun asDirective(
        transcription: SpokenTranscript,
        textOnly: Boolean = false,
        usingAudioModality: Boolean = false,
        chatMessage: Boolean = false,
        promptName: String = "Idle Mode"
    ): VoqalDirective {
        ThreadingAssertions.assertBackgroundThread()
        val selectedTextEditor = project.service<VoqalContextService>().getSelectedTextEditor()
        val currentCode = ReadAction.compute(ThrowableComputable {
            selectedTextEditor?.document?.text
        })
        val selectionModel = selectedTextEditor?.selectionModel
        val selectedText = ReadAction.compute(ThrowableComputable {
            selectionModel?.selectedText?.let {
                SelectedCode(it, selectionModel.selectionStart, selectionModel.selectionEnd)
            }
        })

        val openFiles = project.service<VoqalContextService>().getOpenFiles(selectedTextEditor)
        val configService = project.service<VoqalConfigService>()
        val promptSettings = configService.getPromptSettings(promptName)
        val models = configService.getConfig().languageModelsSettings.models
        val languageModelSettings = models.firstOrNull {
            it.name == promptSettings.modelName
        } ?: models.first()

        val caretOffset = ReadAction.compute(ThrowableComputable {
            val caretModel = selectedTextEditor?.caretModel
            caretModel?.offset
        })
        val caretLine = ReadAction.compute(ThrowableComputable {
            val caretModel = selectedTextEditor?.caretModel
            caretModel?.logicalPosition?.line
        })
        val caretColumn = ReadAction.compute(ThrowableComputable {
            val caretModel = selectedTextEditor?.caretModel
            caretModel?.logicalPosition?.column
        })

        val activeBreakpoints = XDebuggerManager.getInstance(project).breakpointManager.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == selectedTextEditor?.virtualFile?.url }
            .map { (it.sourcePosition?.line ?: it.line) + 1 }

        //val projectCodeStructure = project.service<VoqalSearchService>().getProjectCodeStructure()
        //println(projectCodeStructure)
        val viewingCodeProblems = selectedTextEditor?.let {
            project.service<VoqalSearchService>().getActiveProblems(it)
        }
        val projectFileStructure = project.service<VoqalSearchService>().getProjectStructureAsMarkdownTree()
        val toolService = project.service<VoqalToolService>()
        val languageOfFile = selectedTextEditor?.let {
            val file = it.virtualFile ?: return@let null
            (FileTypeManager.getInstance().getFileTypeByFile(file) as? LanguageFileType)?.language
        }
        val fullCommand = VoqalDirective(
            ide = IdeContext(
                project,
                selectedTextEditor,
                projectFileTree = projectFileStructure,
                //projectCodeStructure = projectCodeStructure
            ),
            internal = InternalContext(
                memorySlice = project.service<VoqalMemoryService>().getCurrentMemory(promptSettings),
                availableActions = toolService.getAvailableTools().values,
                languageModelSettings = languageModelSettings,
                promptSettings = promptSettings,
                speechId = transcription.speechId,
                usingAudioModality = usingAudioModality
            ),
            developer = DeveloperContext(
                transcription = transcription.transcript,
                openFiles = openFiles,
                viewingFile = selectedTextEditor?.virtualFile,
                viewingCode = currentCode?.let {
                    ViewingCode(
                        code = currentCode,
                        language = languageOfFile?.id?.lowercase() ?: "",
                        filename = selectedTextEditor?.virtualFile?.name,
                        caret = selectedTextEditor?.caretModel?.currentCaret,
                        caretOffset = caretOffset,
                        caretLine = caretLine,
                        caretColumn = caretColumn,
                        problems = viewingCodeProblems ?: emptyList()
                    )
                },
                selectedCode = selectedText,
                activeBreakpoints = activeBreakpoints,
                textOnly = textOnly,
                chatMessage = chatMessage
            )
        )
        return project.service<VoqalContextService>().cropAsNecessary(fullCommand)
    }

    suspend fun executeDirective(directive: VoqalDirective) {
        val log = project.getVoqalLogger(this::class)
        ThreadingAssertions.assertBackgroundThread()
        log.debug("Processing directive: ${directive.developer.transcription}")
        val execution = DirectiveExecution(directive)
        activeDirectives[directive] = execution
        listeners.forEach {
            it(this, execution)
        }

        //save current config to disk if desired
        val config = project.service<VoqalConfigService>().getConfig()
        val configFile = File(project.basePath, ".voqal/config.json")
        if (configFile.exists() && configFile.length() == 0L) {
            configFile.writeText(config.toJson().encodePrettily())
            log.debug("Wrote config to file: ${config.withKeysRemoved()}")
        }

        val textOnly = directive.developer.textOnly
        val aiProvider = project.service<VoqalConfigService>().getAiProvider()
        if (!aiProvider.isLlmProvider()) {
            val errorMessage = "No language model provider available"
            log.warn(errorMessage)
            handleResponse(errorMessage, isTextOnly = textOnly)
            finishDirective(execution)
            return
        }

        var handledParseError = false //allow only one parse error
        var retry = false
        var attempt = 0
        val maxAttempts = 10
        try {
            do {
                try {
                    handleResponse(directive.internal.memorySlice.addMessage(directive, !retry))
                    retry = false
                } catch (e: RateLimitException) {
                    execution.errors.add(e)
                    if (++attempt >= maxAttempts) {
                        log.warn("Rate limit exceeded. Maximum retry attempts reached.")
                        handleResponse("Rate limit exceeded. Please try again later.", isTextOnly = textOnly)
                        retry = false
                    } else {
                        val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                        log.warn("Rate limit exceeded. Retrying in ${delayTime / 1000} seconds...")
                        handleResponse(
                            "Rate limit exceeded. Retrying in ${delayTime / 1000} seconds...",
                            isTextOnly = textOnly
                        )
                        delay(delayTime)
                        retry = true
                    }
                } catch (e: OpenAITimeoutException) {
                    execution.errors.add(e)
                    if (++attempt >= maxAttempts) {
                        log.warn("Request timeout. Maximum retry attempts reached.")
                        handleResponse("Request timeout. Please try again later.", isTextOnly = textOnly)
                        retry = false
                    } else {
                        val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                        log.warn("Request timeout. Retrying in ${delayTime / 1000} seconds...")
                        handleResponse(
                            "Request timeout. Retrying in ${delayTime / 1000} seconds...",
                            isTextOnly = textOnly
                        )
                        delay(delayTime)
                        retry = true
                    }
                } catch (e: OpenAIAPIException) {
                    execution.errors.add(e)
                    if (e.statusCode in setOf(503)) {
                        if (++attempt >= maxAttempts) {
                            log.warn("LLM API error. Maximum retry attempts reached.")
                            handleResponse("LLM API error. Please try again later.", isTextOnly = textOnly)
                            retry = false
                        } else {
                            val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                            log.warn("LLM API error. Retrying in ${delayTime / 1000} seconds...")
                            handleResponse(
                                "LLM API error. Retrying in ${delayTime / 1000} seconds...",
                                isTextOnly = textOnly
                            )
                            delay(delayTime)
                            retry = true
                        }
                    } else {
                        val errorMessage = e.message ?: "An unknown error occurred"
                        log.warn(errorMessage)
                        handleResponse(errorMessage, isTextOnly = textOnly)
                        retry = false
                    }
                } catch (e: OpenAIException) {
                    execution.errors.add(e)
                    val errorMessage = e.message ?: "An unknown error occurred"
                    log.warn(errorMessage)
                    handleResponse(errorMessage, isTextOnly = textOnly)
                    retry = false
                } catch (e: ResponseParseError) {
                    execution.errors.add(e)
                    if (!handledParseError) {
                        log.warn("Response parse error: ${e.message}. Retrying...")
                        handledParseError = true
                        //todo: this is very hacky
                        (directive.internal.memorySlice as LocalMemorySlice).messageList.add(
                            ChatMessage(
                                ChatRole.User,
                                TextContent(content = e.message)
                            )
                        )
                        retry = true
                    } else {
                        val errorMessage = e.message
                        log.warn(errorMessage)
                        handleResponse(errorMessage, isTextOnly = true)
                        retry = false
                    }
                } catch (e: Exception) {
                    execution.errors.add(e)
                    val errorMessage = e.message ?: "An unknown error occurred"
                    log.error(errorMessage, e)
                    handleResponse(errorMessage, isTextOnly = textOnly)
                    retry = false
                }
            } while (retry)
        } finally {
            finishDirective(execution)
        }
    }

    suspend fun handleResponse(
        input: String,
        tts: TextToSpeechSettings? = null,
        isTextOnly: Boolean = false,
        response: VoqalResponse? = null
    ) {
        project.service<VoqalStatusService>().updateText(input, response)
        if (!isTextOnly) {
            project.service<VoqalVoiceService>().playVoiceAndWait(input, tts)
        }
    }

    private fun finishDirective(directive: VoqalDirective) {
        val log = project.getVoqalLogger(this::class)
        val execution = activeDirectives[directive]
        if (execution == null) {
            log.warn("No active directive found for response: $directive")
            return
        }
        finishDirective(execution)
    }

    private fun finishDirective(execution: DirectiveExecution) {
        val log = project.getVoqalLogger(this::class)
        if (activeDirectives.remove(execution.directive) != null) {
            execution.endTime = System.currentTimeMillis()
            executionHistory.add(execution)
            listeners.forEach {
                it(this, execution)
            }
            log.debug("Finished directive ${execution.directive.directiveId} in ${execution.duration}ms")
        }
    }

    private suspend fun handleResponse(response: VoqalResponse) {
        val log = project.getVoqalLogger(this::class)
        log.debug("Handling response for directive: ${response.directive.directiveId}")
        ThreadingAssertions.assertBackgroundThread()

        val execution = activeDirectives[response.directive]
        if (execution != null) {
            log.debug("Updating directive listeners: ${response.directive.directiveId}")
            execution.response = response
            listeners.forEach {
                it(this, execution)
            }
            log.debug("Updated directive listeners: ${response.directive.directiveId}")
        } else {
            log.warn("No active directive execution found for response: ${response.directive.directiveId}")
        }

        val toolCalls = response.toolCalls
        if (toolCalls.isEmpty()) {
            log.warn("No tool calls available: $response")
            finishDirective(response.directive)
            handleResponse(
                "No tool calls available: $response"
            )
        } else {
            val executionStr = buildString {
                append("Executing ")
                append(toolCalls.size)
                append(" tool calls: ")
                append(toolCalls.joinToString {
                    if (it is ToolCall.Function) {
                        it.function.name
                    } else {
                        it.toString()
                    }
                })
            }
            project.service<VoqalStatusService>().updateText(executionStr, response)
            log.debug(executionStr)

            val toolService = project.service<VoqalToolService>()
            var sortedToolCalls = VoqalTool.asSortedToolCalls(toolCalls).toMutableList()
//            if (sortedToolCalls.size == 1 && (sortedToolCalls.first() as? ToolCall.Function)?.function?.name == AnswerQuestionTool.NAME) {
//                //if there is only one tool call and it is the answer question tool, execute it immediately
//                log.debug("Executing directive answer question tool immediately")
//                val function = sortedToolCalls.first() as ToolCall.Function
//                sortedToolCalls = mutableListOf(function.copy(
//                    function = function.function.copy(
//                        argumentsOrNull = JsonObject(function.function.arguments)
//                            .put("executeImmediately", true)
//                            .toString()
//                    )
//                ))
//            }

            //ensure answer question tool is last
            val answerQuestionTool = sortedToolCalls.find {
                (it as? ToolCall.Function)?.function?.name == AnswerQuestionTool.NAME
            }
            if (answerQuestionTool != null) {
                sortedToolCalls.remove(answerQuestionTool)
                sortedToolCalls.add(answerQuestionTool)
            }

            sortedToolCalls.forEach {
                val toolCall = it as? ToolCall.Function
                if (toolCall != null) {
                    toolService.handleFunctionCall(toolCall, response)
                } else {
                    log.warn("Missing function call. Message: $response")
                    handleResponse(
                        "Missing function call. Message: $response"
                    )
                    return@forEach
                }
            }
            finishDirective(response.directive)
        }
    }
}
