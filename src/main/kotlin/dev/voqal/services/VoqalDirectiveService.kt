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
import com.intellij.openapi.editor.Inlay
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
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.code.SelectedCode
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.flaw.error.parse.ResponseParseError
import dev.voqal.assistant.focus.DirectiveExecution
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.memory.local.LocalMemorySlice
import dev.voqal.config.settings.TextToSpeechSettings
import dev.voqal.ide.actions.ShowQuickEditAction.Companion.USER_DIRECTIVE_TEXT_AREA
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowContentManager
import dev.voqal.status.VoqalStatus.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import java.io.File
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException
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

    private val log = project.getVoqalLogger(this::class)
    private val activeDirectives = mutableMapOf<VoqalDirective, DirectiveExecution>()
    private val executionHistory = mutableListOf<DirectiveExecution>()
    private val listeners = CopyOnWriteArrayList<(VoqalDirectiveService, DirectiveExecution) -> Unit>()

    init {
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

    suspend fun handlePartialTranscription(spokenTranscript: SpokenTranscript) {
        if (project.service<VoqalStatusService>().getStatus() == EDITING) {
            val inlay = project.service<VoqalMemoryService>().getUserData("voqal.edit.inlay") as Inlay<*>?
            if (inlay != null) {
                val textArea = inlay.getUserData(USER_DIRECTIVE_TEXT_AREA)?.textArea
                if (textArea != null) {
                    project.invokeLater {
                        textArea.text = spokenTranscript.transcript
                        textArea.caretPosition = textArea.text.length
                    }
                }
            }
        }
        project.service<ChatToolWindowContentManager>()
            .updateDirectiveInput(spokenTranscript.transcript)
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
        if (!usingAudioModality) {
            if (spokenTranscript.transcript.isBlank()) {
                log.debug("Ignoring empty transcription")
                return
            }

            if (project.service<VoqalStatusService>().getStatus() == EDITING) {
                val inlay = project.service<VoqalMemoryService>().getUserData("voqal.edit.inlay") as Inlay<*>?
                if (inlay != null) {
                    val textArea = inlay.getUserData(USER_DIRECTIVE_TEXT_AREA)?.textArea
                    if (textArea != null && spokenTranscript.transcript.startsWith(textArea.text)) {
                        project.invokeLater {
                            textArea.text = ""
                        }
                    }
                }
            }

            //add to chat window
            val chatContentManager = project.service<ChatToolWindowContentManager>()
            if (spokenTranscript.transcript.startsWith(chatContentManager.getDirectiveInput())) {
                chatContentManager.updateDirectiveInput("")
            }
            chatContentManager.addUserMessage(spokenTranscript.transcript, spokenTranscript.speechId)

            //final intent check
            val detectedIntent = project.service<VoqalToolService>().intentCheck(spokenTranscript)
            if (detectedIntent != null) {
                log.debug("Blind executing detected intent: ${spokenTranscript.transcript}")
                project.service<VoqalToolService>().blindExecute(
                    detectedIntent.executable, io.vertx.core.json.JsonObject.mapFrom(detectedIntent.args), chatMessage
                )
                return
            }
        }

        val aiProvider = project.service<VoqalConfigService>().getAiProvider()
        if (!aiProvider.isLlmProvider()) {
            log.warnChat("No language model provider available")
            return
        }

        //send transcription to LLM
        val currentStatus = project.service<VoqalStatusService>().getStatus()
        if (currentStatus == IDLE) {
            val directive = createDirective(
                transcription = spokenTranscript,
                textOnly = textOnly,
                usingAudioModality = usingAudioModality,
                chatMessage = chatMessage,
                promptName = "Idle Mode"
            )
            executeDirective(directive)
        } else if (currentStatus == EDITING) {
            val directive = createDirective(
                transcription = spokenTranscript,
                textOnly = textOnly,
                usingAudioModality = usingAudioModality,
                chatMessage = chatMessage,
                promptName = "Edit Mode"
            )
            executeDirective(directive)
        } else if (currentStatus == SEARCHING) {
            val directive = createDirective(
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

    fun createDirective(
        transcription: SpokenTranscript,
        textOnly: Boolean = false,
        usingAudioModality: Boolean = false,
        chatMessage: Boolean = false,
        promptName: String = "Idle Mode"
    ): VoqalDirective {
        ThreadingAssertions.assertBackgroundThread()
        val contextService = project.service<VoqalContextService>()
        val selectedTextEditor = contextService.getSelectedTextEditor()
        val currentCode = ReadAction.compute(ThrowableComputable {
            selectedTextEditor?.document?.text
        })
        val selectionModel = selectedTextEditor?.selectionModel
        val selectedText = ReadAction.compute(ThrowableComputable {
            selectionModel?.selectedText?.let {
                SelectedCode(it, selectionModel.selectionStart, selectionModel.selectionEnd)
            }
        })

        val openFiles = contextService.getOpenFiles(selectedTextEditor)
        val configService = project.service<VoqalConfigService>()
        val promptSettings = configService.getPromptSettings(promptName)
        val languageModelSettings = configService.getLanguageModelSettings(promptSettings)

        val activeBreakpoints = XDebuggerManager.getInstance(project).breakpointManager.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == selectedTextEditor?.virtualFile?.url }
            .map { (it.sourcePosition?.line ?: it.line) + 1 }

        val searchService = project.service<VoqalSearchService>()
        val viewingCodeProblems = selectedTextEditor?.let { searchService.getActiveProblems(it) }
        val projectFileStructure = contextService.getProjectStructureAsMarkdownTree()
        val languageOfFile = selectedTextEditor?.let {
            val file = it.virtualFile ?: return@let null
            (FileTypeManager.getInstance().getFileTypeByFile(file) as? LanguageFileType)?.language
        }
        val fullDirective = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = project.service<VoqalMemoryService>().getCurrentMemory(promptSettings),
                availableActions = project.service<VoqalToolService>().getAvailableTools().values,
                languageModelSettings = languageModelSettings,
                promptSettings = promptSettings,
                speechId = transcription.speechId,
                usingAudioModality = usingAudioModality
            ),
            ide = IdeContext(
                project,
                selectedTextEditor,
                projectFileTree = projectFileStructure
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
                        problems = viewingCodeProblems ?: emptyList()
                    )
                },
                selectedCode = selectedText,
                activeBreakpoints = activeBreakpoints,
                textOnly = textOnly,
                chatMessage = chatMessage
            )
        )
        return contextService.cropAsNecessary(fullDirective)
    }

    suspend fun executeDirective(directive: VoqalDirective) {
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
                    handleResponse(directive.assistant.memorySlice.addMessage(directive, !retry))
                    retry = false
                } catch (e: RateLimitException) {
                    execution.errors.add(e)
                    if (++attempt >= maxAttempts) {
                        log.warnChat("Rate limit exceeded. Maximum retry attempts reached.")
                        retry = false
                    } else {
                        val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                        log.warnChat("Rate limit exceeded. Retrying in ${delayTime / 1000} seconds...")
                        delay(delayTime)
                        retry = true
                    }
                } catch (e: OpenAITimeoutException) {
                    execution.errors.add(e)
                    if (++attempt >= maxAttempts) {
                        log.warnChat("Request timeout. Maximum retry attempts reached.")
                        retry = false
                    } else {
                        val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                        log.warnChat("Request timeout. Retrying in ${delayTime / 1000} seconds...")
                        delay(delayTime)
                        retry = true
                    }
                } catch (e: OpenAIAPIException) {
                    execution.errors.add(e)
                    if (e.statusCode in setOf(503)) {
                        if (++attempt >= maxAttempts) {
                            log.warnChat("LLM API error. Maximum retry attempts reached.")
                            retry = false
                        } else {
                            val delayTime = (2.0.pow(attempt.toDouble()) * 1000).toLong()
                            log.warnChat("LLM API error. Retrying in ${delayTime / 1000} seconds...")
                            delay(delayTime)
                            retry = true
                        }
                    } else {
                        val errorMessage = e.message ?: "An unknown error occurred"
                        log.warnChat(errorMessage)
                        retry = false
                    }
                } catch (e: OpenAIException) {
                    execution.errors.add(e)
                    val errorMessage = if (e.cause is ConnectException) {
                        "Host connection unavailable"
                    } else e.message ?: "An unknown error occurred"
                    log.warnChat(errorMessage)
                    retry = false
                } catch (e: ResponseParseError) {
                    execution.errors.add(e)
                    if (!handledParseError) {
                        log.warn("Response parse error: ${e.message}. Retrying...")
                        handledParseError = true
                        //todo: this is very hacky
                        (directive.assistant.memorySlice as LocalMemorySlice).messageList.add(
                            ChatMessage(
                                ChatRole.User,
                                TextContent(content = e.message)
                            )
                        )
                        retry = true
                    } else {
                        val errorMessage = e.message
                        log.warnChat(errorMessage)
                        retry = false
                    }
                } catch (e: UnresolvedAddressException) {
                    execution.errors.add(e)
                    val errorMessage = "Host connection unavailable"
                    log.warnChat(errorMessage)
                    retry = false
                } catch (e: Exception) {
                    execution.errors.add(e)
                    val errorMessage = e.message ?: "An unknown error occurred"
                    log.errorChat(errorMessage, e)
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
        val execution = activeDirectives[directive]
        if (execution == null) {
            log.warn("No active directive found for response: $directive")
            return
        }
        finishDirective(execution)
    }

    private fun finishDirective(execution: DirectiveExecution) {
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
            log.warnChat("No tool calls provided")
            finishDirective(response.directive)
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
            if (toolCalls.size > 1) {
                project.service<VoqalStatusService>().updateText(executionStr, response)
            }
            log.debug(executionStr)

            val toolService = project.service<VoqalToolService>()
            toolCalls.forEach {
                val toolCall = it as? ToolCall.Function
                if (toolCall != null) {
                    toolService.handleFunctionCall(toolCall, response)
                } else {
                    log.warnChat("Missing function call")
                    return@forEach
                }
            }
            finishDirective(response.directive)
        }
    }
}
