package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
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
import dev.voqal.utils.WordsToNumbersUtil
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

class SelectLineTool : VoqalTool() {

    companion object {
        const val NAME = "select_line"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val line = args.getString("line").toString().toInt() - 1
        log.debug("Triggering select line $line")

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        WriteCommandAction.runWriteCommandAction(editor.project, ThrowableComputable {
            val document = editor.document
            val lineStart = document.getLineStartOffset(line)
            val lineEnd = document.getLineEndOffset(line)
            editor.selectionModel.setSelection(lineStart, lineEnd)
            project.scope.launch {
                project.service<VoqalStatusService>().updateText("Selected line: ${line + 1}")
            }
        })
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        val parsedText = WordsToNumbersUtil.convertTextualNumbersInDocument(rawString)

        //needs to be in "select line x" format (ignore casing and punctuation)
        val regex = Regex("^select(?: the)? line? (\\d+)$", RegexOption.IGNORE_CASE)
        val match = regex.find(parsedText)
        if (match != null) {
            val line = match.groupValues[1]
            return Pair(NAME, mapOf("line" to line))
        } else {
            return null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Select the line at the specified position.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("line", JsonObject().apply {
                    put("type", "integer")
                    put("description", "The line number to select.")
                })
            })
            put("required", JsonArray().add("line"))
        }.toString())
    )
}
