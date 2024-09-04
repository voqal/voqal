package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import dev.voqal.services.scope
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.launch

class AddBreakpointsTool : VoqalTool() {

    companion object {
        const val NAME = "add_breakpoints"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }
        val lineNumbers = args.getJsonArray("line_numbers").map { it.toString().toInt() - 1 }
        log.info("Adding breakpoint to lines: $lineNumbers")

        val promise = Promise.promise<Void>()
        project.invokeLater {
            addLineBreakpoints(project, editor, lineNumbers)

            project.scope.launch {
                project.service<VoqalStatusService>().updateText("Tool $name")
                promise.complete()
            }
        }
        promise.future().coAwait()
    }

    private fun addLineBreakpoints(project: Project, editor: Editor, lines: List<Int>) {
        lines.forEach { line ->
            //todo: ignore if breakpoint already exists

            val action = ActionManager.getInstance().getAction(IdeActions.ACTION_TOGGLE_LINE_BREAKPOINT)
                ?: throw AssertionError("'" + IdeActions.ACTION_TOGGLE_LINE_BREAKPOINT + "' action not found")
            val dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, project)
                .add(CommonDataKeys.EDITOR, editor)
                .add(XLineBreakpointManager.BREAKPOINT_LINE_KEY, line)
                .build()
            val event = AnActionEvent.createFromAnAction(action, null, ActionPlaces.EDITOR_GUTTER, dataContext)
            ActionUtil.performActionDumbAwareWithCallbacks(action, event)
        }
    }

    override fun supportsDirectiveMode(): Boolean {
        return true
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            if (directive.assistant.directiveMode) {
                append("Adds breakpoints to the specified directive. ")
                append("Do not include line number(s) unless strictly instructed.")
            } else {
                append("Adds breakpoints to the specified line numbers.")
            }
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                if (directive.assistant.directiveMode) {
                    put("directive", JsonObject().apply {
                        put("type", "string")
                        put("description", "The directive to pass to the tool")
                    })
                } else {
                    put("line_numbers", JsonObject().apply {
                        put("type", "array")
                        put("description", "The line number(s) to add a breakpoint to.")
                        put("items", JsonObject().put("type", "string"))
                    })
                }
            })
            if (directive.assistant.directiveMode) {
                put("required", JsonArray().add("directive"))
            } else {
                put("required", JsonArray().add("line_numbers"))
            }
        }.toString())
    )
}
