package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.AnswerQuestionTool
import dev.voqal.services.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch
import kotlin.math.max

class ShowCodeTool : VoqalTool() {

    companion object {
        const val NAME = "show_code"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val resultText = args.getString("result")

        //todo: should be a tool?
        val question = Regex("Question: (.*)").find(resultText)?.groupValues?.get(1)
        if (question != null) {
            project.service<VoqalToolService>().blindExecute(
                AnswerQuestionTool(), JsonObject().put("answer", question)
            )
            return
        }

        val qualifiedName = Regex("Qualified name: (.*)").find(resultText)?.groupValues?.get(1)
        val lineRange = Regex("Line range: (\\d+)-(\\d+)").find(resultText)?.groupValues?.let {
            it[1].toInt()..it[2].toInt()
        }
        log.debug("qualifiedName: $qualifiedName")
        log.debug("lineRange: $lineRange")

        //open file
        var file = project.service<VoqalSearchService>().findFile(qualifiedName!!)
        if (file == null) {
            val modifiedName = qualifiedName.substringBeforeLast('.')
            log.warn("Trying without last part of qualified name: $modifiedName")
            file = project.service<VoqalSearchService>().findFile(modifiedName)
        }
        if (file == null) {
            val modifiedName = qualifiedName.substringBeforeLast('#')
            log.warn("Trying without last part of qualified name: $modifiedName")
            file = project.service<VoqalSearchService>().findFile(modifiedName)
        }
        if (file != null) {
            project.invokeLater {
                val fileEditor = FileEditorManager.getInstance(project).openFile(file, true).first()
                val editor = (fileEditor as TextEditor).editor

                //jump to line
                val lineNumber = lineRange?.first ?: 0
                ApplicationManager.getApplication().invokeAndWait {
                    val logicalPosition = LogicalPosition(max(0, lineNumber - 1), 0) //intellij is 0-based
                    editor.caretModel.moveToLogicalPosition(logicalPosition)
                    editor.scrollingModel.scrollToCaret(ScrollType.CENTER)

                    //highlight lines
                    project.scope.launch {
                        project.service<VoqalToolService>().blindExecute(
                            SelectLinesTool(), JsonObject()
                                .put("startLine", lineRange?.first)
                                .put("endLine", lineRange?.last)
                        )
                    }
                }
            }
        } else {
            log.warn("File not found for qualified name: $qualifiedName")
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        return directive.internal.promptSettings?.promptName?.lowercase() == "search mode"
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Shows code (functions, variables, etc.) based on the fully qualified name.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("qualifiedName", JsonObject().apply {
                    put("type", "string")
                    put("description", "The fully qualified name of the code to show.")
                })
            })
            put("required", JsonArray().add("qualifiedName"))
        }.toString())
    )
}
