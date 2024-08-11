package dev.voqal.services

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.ToolCall
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.ThreadingAssertions
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.InternalContext
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.code.CreateClassTool
import dev.voqal.assistant.tool.code.MoveClassTool
import dev.voqal.assistant.tool.custom.ExecTool
import dev.voqal.assistant.tool.ide.*
import dev.voqal.assistant.tool.ide.navigation.*
import dev.voqal.assistant.tool.system.*
import dev.voqal.assistant.tool.system.mode.ToggleEditModeTool
import dev.voqal.assistant.tool.system.mode.ToggleSearchModeTool
import dev.voqal.assistant.tool.text.*
import dev.voqal.config.settings.LanguageModelSettings
import io.vertx.core.json.JsonObject
import org.yaml.snakeyaml.Yaml
import java.io.StringReader
import java.util.regex.Matcher

@Service(Service.Level.PROJECT)
class VoqalToolService(private val project: Project) {

    companion object {
        fun fixIllegalDollarEscape(jsonString: String): String {
            val regex = """\\\$""".toRegex()
            return regex.replace(jsonString, Matcher.quoteReplacement("\$"))
        }

        fun parseYaml(yamlString: String): Map<String, Any> {
            val yaml = Yaml()
            val reader = StringReader(yamlString)
            return yaml.load(reader) as Map<String, Any>
        }
    }

    private val availableIntents = setOf(
        DeleteTool(),
        BackspaceTool(),
        TabTool(),
        CloseFileTool(),
        CreateFileTool(),
        GotoLineTool(),
        GotoTextTool(),
        GotoNextTool(),
        GotoPreviousTool(),
        HideIdeTool(),
        HideToolWindowTool(),
        NewLineTool(),
        DeleteLineTool(),
        ScrollTool(),
        UndoTool(),
        RedoTool(),
        ShowIdeTool(),
        ShowToolWindowTool(),
        StopListeningTool(),
        ToggleEditModeTool(),
        SelectAllTool(),
        UnselectTool(),
        CancelTool(),
        LooksGoodTool(),
        EndOfLineTool(),
        StartOfLineTool(),
        DeleteLinesTool(),
        SelectLinesTool(),
        SelectLineTool(),
        PreviousTabTool(),
        NextTabTool(),
        ChangeThemeTool(),
        ClearChatTool(),
        CreateClassTool(),
        MoveClassTool(),
        AddBreakpointsTool(),
        OpenFileTool(),
        PlayProgramTool(),
        RemoveBreakpointsTool(),
        RunProgramTool(),
        ShowTabTool(),
        StopProgramTool(),
        SmartEnterTool(),
        AnswerQuestionTool(),
        IgnoreTool(),
        ViewSourceTool(),
        ZoomInIdeTool(),
        ZoomOutIdeTool(),
        EditTextTool(),
        ToggleZenModeTool(),
        ShowCodeTool(),
        ToggleSearchModeTool(),
    )
    private val availableTools = availableIntents
    private val availableToolsMap = availableTools.associateBy { it.name }

    fun getAvailableTools(): Map<String, VoqalTool> {
        return availableTools.associateBy { it.name }.filter { (_, _) ->
            when {
                else -> true //action.isVisible(project, editor) //todo: this?
            }
        }
    }

    private fun getIntentAction(intent: String): VoqalTool? {
        var intentAction: Any? = ActionManager.getInstance().getAction("voqal.$intent")
        if (intentAction == null) {
            intentAction = availableIntents.firstOrNull { it.name == intent }
        }
        if (intentAction == null) {
            intentAction = ActionManager.getInstance().getAction(intent)
        }
        if (intentAction == null) {
            intentAction = ActionManager.getInstance().getAction(intent + "Action")
        }
        if (intentAction == null) {
            intentAction = ActionManager.getInstance().getAction("$$intent")
        }
        return intentAction as? VoqalTool
    }

    suspend fun intentCheck(spokenTranscript: SpokenTranscript): DetectedIntent? {
        var detectedIntent: DetectedIntent? = null
        for (intent in availableIntents) {
            detectedIntent = intent.getTranscriptIntent(project, spokenTranscript)
            if (detectedIntent != null) {
                break
            }
        }
        return detectedIntent
    }

    fun executeAnAction(args: Map<String, Any>, action: AnAction) {
        project.invokeLater {
            var editor = FileEditorManager.getInstance(project).selectedTextEditor
            if (editor == null) {
                val virtualFile = FileEditorManager.getInstance(project).selectedEditor?.file
                editor = EditorFactory.getInstance().allEditors.find { it.virtualFile == virtualFile }
            }
            val dataContext = SimpleDataContext.builder()
                .apply {
                    add(PlatformDataKeys.PROJECT, project)
                    add(CommonDataKeys.EDITOR, editor)
                    add(PlatformCoreDataKeys.FILE_EDITOR, FileEditorManager.getInstance(project).selectedEditor)
                    args.forEach {
                        add(DataKey.create(it.key), it.value)
                    }
                }
                .build()
            val inputEvent = null
            val actionEvent = AnActionEvent(
                inputEvent,
                dataContext,
                "Voqal",
                action.templatePresentation.clone(),
                ActionManager.getInstance(),
                0
            )
            action.actionPerformed(actionEvent) //AnActions are executed on the EDT
        }
    }

    suspend fun blindExecute(tool: VoqalTool, args: JsonObject = JsonObject(), chatMessage: Boolean = false) {
        val editor = project.service<VoqalContextService>().getSelectedTextEditor()
        val mockDirective = VoqalDirective(
            IdeContext(project, editor),
            InternalContext(
                memorySlice = object : MemorySlice {
                    override val id: String
                        get() = throw UnsupportedOperationException("Not supported")

                    override suspend fun addMessage(directive: VoqalDirective, addMessage: Boolean): VoqalResponse =
                        throw UnsupportedOperationException("Not supported")
                },
                availableActions = emptyList(),
                languageModelSettings = LanguageModelSettings()
            ),
            DeveloperContext("", chatMessage = chatMessage)
        )
        tool.actionPerformed(args, mockDirective)
    }

    private suspend fun executeIntent(args: Map<String, Any>, intentAction: VoqalTool) {
        val directive = project.service<VoqalDirectiveService>()
            .asDirective(SpokenTranscript("", null), true) //todo: memory/speech id
        intentAction.actionPerformed(JsonObject.mapFrom(args), directive)
    }

    suspend fun handleFunctionCall(toolCall: ToolCall.Function, response: VoqalResponse) {
        val log = project.getVoqalLogger(this::class)
        val functionCall = toolCall.function
        var action = availableToolsMap[functionCall.name]
        if (action == null) {
            //AnswerQuestion -> answer_question
            action = availableToolsMap[functionCall.name.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()]
        }
        if (action == null) {
            action = getCustomTools(response.directive)[functionCall.name]
        }
        if (action != null) {
            log.info("Invoking tool: ${functionCall.name}")
            project.service<VoqalStatusService>().updateText(
                "Invoking tool: ${functionCall.name} (Directive: ${response.directive.internal.directiveMode})",
                response
            )
            invokeAction(functionCall, action, response)
            project.service<VoqalStatusService>().updateText(
                "Finished invoking tool: ${functionCall.name} (Directive: ${response.directive.internal.directiveMode})",
                response
            )
        } else {
            val intent = getIntentAction(functionCall.name)
            if (intent != null) {
                log.info("Invoking intent: ${functionCall.name}")
                project.service<VoqalStatusService>().updateText("Invoking intent: ${functionCall.name}", response)
                val args = mutableMapOf<String, Any>()
                val jsonArgs = JsonObject(fixIllegalDollarEscape(functionCall.arguments))
                jsonArgs.fieldNames().forEach {
                    args[it] = jsonArgs.getValue(it)
                }
                executeIntent(args, intent)
                project.service<VoqalStatusService>()
                    .updateText("Finished invoking intent: ${functionCall.name}", response)
            } else {
                log.warn("Unable to find tool: ${functionCall.name}")
                project.service<VoqalDirectiveService>().handleResponse(
                    "Unable to find tool: ${functionCall.name}"
                )
            }
        }
    }

    private fun getCustomTools(directive: VoqalDirective): MutableMap<String, VoqalTool> {
        val log = project.getVoqalLogger(this::class)
        val customTools = mutableMapOf<String, VoqalTool>()
        val regexPattern = "```yaml\\r?\\n(.*?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = regexPattern.findAll(directive.toMarkdown())
        matches.forEach {
            val yamlContent = it.groups[1]?.value ?: ""
            try {
                val map = parseYaml(yamlContent)
                if (map["type"] == "function") {
                    val funcMap = map["function"] as Map<*, *>
                    if (funcMap["exec"] != null) {
                        val toolName = funcMap["name"].toString()
                        val execTool = ExecTool(
                            toolName,
                            (funcMap["exec"] as Map<*, *>)["command"].toString(),
                            (funcMap["exec"] as Map<*, *>)["type"]?.toString()
                        )
                        customTools[toolName] = execTool
                    }
                }
            } catch (_: Exception) {
                log.warn("Failed to parse: $yamlContent")
            }
        }
        return customTools
    }

    private suspend fun invokeAction(functionCall: FunctionCall, action: VoqalTool, response: VoqalResponse) {
        val log = project.getVoqalLogger(this::class)
        ThreadingAssertions.assertBackgroundThread()
        val args = try {
            JsonObject(fixIllegalDollarEscape(functionCall.arguments))
        } catch (e: Exception) {
            log.warn("Failed to parse: ${functionCall.arguments}. Reason: ${e.message}")
            project.service<VoqalDirectiveService>().handleResponse(
                "Failed to parse: ${functionCall.name}. Please try again."
            )
            return
        }
        try {
            log.debug("Invoking action: ${functionCall.name} - Args: $args")
            val isDirective = args.containsKey("directive")
            val canShortcutDirective = isDirective && action.canShortcut(project, functionCall)
            if (isDirective && !canShortcutDirective) {
                var childDirective = project.service<VoqalDirectiveService>()
                    .asDirective(SpokenTranscript(args.getString("directive"), null), true)
                childDirective = childDirective.copy(
                    internal = childDirective.internal.copy(
                        directiveMode = false,
                        availableActions = childDirective.internal.availableActions.filter {
                            it.name == functionCall.name
                        },
                        parentDirective = response.directive
                    ),
                    developer = childDirective.developer.copy(
                        relevantFiles = response.directive.developer.relevantFiles
                    )
                )

                project.service<VoqalDirectiveService>().executeDirective(childDirective)
            } else {
                action.actionPerformed(args, response.directive)

                val memoryService = project.service<VoqalMemoryService>()
                memoryService.putLongTermUserData("last_executed_tool", action.name)
                memoryService.putLongTermUserData("last_executed_tool_args", args)
            }
        } catch (e: Throwable) {
            log.error("Failed to invoke action: ${functionCall.name}", e)
            project.service<VoqalDirectiveService>().handleResponse(
                "Failed to invoke action: ${functionCall.name}. Reason: ${e.message}"
            )
        }
    }
}
