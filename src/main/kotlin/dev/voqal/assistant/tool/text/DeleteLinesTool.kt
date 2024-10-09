package dev.voqal.assistant.tool.text

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

class DeleteLinesTool : VoqalTool() {

    companion object {
        const val NAME = "delete_lines"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val startLine = args.getString("startLine").toString().toInt()
        val endLine = args.getString("endLine").toString().toInt()
        log.debug("Triggering delete lines $startLine to $endLine")

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        WriteCommandAction.runWriteCommandAction(editor.project, ThrowableComputable {
            val document = editor.document
            val lineStart = document.getLineStartOffset(startLine - 1)
            val lineEnd = document.getLineEndOffset(endLine - 1)
            document.deleteString((lineStart - 1).coerceAtLeast(0), lineEnd)
            project.scope.launch {
                project.service<VoqalStatusService>().updateText("Deleted lines: $startLine to $endLine")
            }
        })
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        val parsedText = WordsToNumbersUtil.convertTextualNumbersInDocument(rawString)

        //needs to be in "delete lines x to y" format (ignore casing and punctuation)
        val regex = Regex("^delete(?: the)? lines (\\d+) to (\\d+)$", RegexOption.IGNORE_CASE)
        val match = regex.find(parsedText)
        if (match != null) {
            val startLine = match.groupValues[1]
            val endLine = match.groupValues[2]
            return Pair(NAME, mapOf("startLine" to startLine, "endLine" to endLine))
        } else if (parsedText.startsWith("delete lines")) {
            //early detection to prevent DeleteIntent from being triggered while finishing transcription
            return Pair(NAME, mapOf("incomplete" to "true"))
        } else if (parsedText.startsWith("delete the lines")) {
            //early detection to prevent DeleteIntent from being triggered while finishing transcription
            return Pair(NAME, mapOf("incomplete" to "true"))
        } else {
            return null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Delete text between the specified lines.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("startLine", JsonObject().apply {
                    put("type", "integer")
                    put("description", "The starting line number to delete.")
                })
                put("endLine", JsonObject().apply {
                    put("type", "integer")
                    put("description", "The ending line number to delete.")
                })
            })
            put("required", JsonArray().add("startLine").add("endLine"))
        }.toString())
    )
}
