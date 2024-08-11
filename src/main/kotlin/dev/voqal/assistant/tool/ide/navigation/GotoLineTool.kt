package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.utils.WordsToNumbersUtil
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.math.max

class GotoLineTool : VoqalTool() {

    companion object {
        const val NAME = "goto_line"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val lineNumber = args.getString("lineNumber").toString().toInt()
        val editor = directive.ide.editor
        if (editor != null) {
            log.info("Going to line: $lineNumber")

            ApplicationManager.getApplication().invokeAndWait {
                val logicalPosition = LogicalPosition(max(0, lineNumber - 1), 0) //intellij is 0-based
                editor.caretModel.moveToLogicalPosition(logicalPosition)
                editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
            }

            project.service<VoqalStatusService>().updateText("Goto line: $lineNumber")
        } else {
            log.warn("No selected text editor")
            project.service<VoqalStatusService>().updateText("No selected text editor")
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        val parsedText = WordsToNumbersUtil.convertTextualNumbersInDocument(rawString)

        //needs to be in "go to line 123" format (ignore casing and punctuation)
        val regex = Regex("^go to line (\\d+)$", RegexOption.IGNORE_CASE)
        val match = regex.find(parsedText)
        if (match != null) {
            val lineNumber = match.groupValues[1]
            return Pair(NAME, mapOf("lineNumber" to lineNumber))
        } else {
            return null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Go to the specified line number.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("lineNumber", JsonObject().apply {
                    put("type", "integer")
                    put("description", "The line number to go to.")
                })
            })
            put("required", JsonArray().add("lineNumber"))
        }.toString())
    )
}
