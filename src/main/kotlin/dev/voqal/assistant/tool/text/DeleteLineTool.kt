package dev.voqal.assistant.tool.text

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.scope
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

class DeleteLineTool : VoqalTool() {

    companion object {
        const val NAME = "delete_line"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        var line = args.getString("line")
        if (line == null) {
            log.debug("No line specified. Using current line.")
            line = ReadAction.compute(ThrowableComputable {
                val caretModel = editor.caretModel
                val caretOffset = caretModel.offset
                val document = editor.document
                val lineNumber = document.getLineNumber(caretOffset)
                (lineNumber + 1).toString()
            })
        }
        log.debug("Triggering delete line: $line")

        WriteCommandAction.runWriteCommandAction(editor.project, ThrowableComputable {
            val document = editor.document
            val lineStart = document.getLineStartOffset(line.toInt() - 1)
            val lineEnd = document.getLineEndOffset(line.toInt() - 1)
            document.deleteString((lineStart - 1).coerceAtLeast(0), lineEnd)
            project.scope.launch {
                project.service<VoqalStatusService>().updateText("Deleted line: $line")
            }
        })
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "delete line") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Delete the line at the specified position.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("line", JsonObject().apply {
                    put("type", "integer")
                    put("description", "The line number to delete.")
                })
            })
            put("required", JsonArray().add("line"))
        }.toString())
    )
}
