package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject

class StartOfLineTool : VoqalTool() {

    companion object {
        const val NAME = "start_of_line"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val editor = directive.ide.editor
        if (editor != null) {
            val lineNumber = ReadAction.compute(ThrowableComputable {
                editor.document.getLineNumber(editor.caretModel.offset)
            })
            log.info("Going to start of line: ${lineNumber + 1}")
            ApplicationManager.getApplication().invokeAndWait {
                editor.caretModel.moveToOffset(editor.document.getLineStartOffset(lineNumber))
                editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
            }

            project.service<VoqalStatusService>().updateText("Start of line: ${lineNumber + 1}")
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
        return when (rawString) {
            "start of the line" -> Pair(NAME, mapOf())
            "start the line" -> Pair(NAME, mapOf())
            "start of line" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Move the cursor to the start of the current line",
        parameters = Parameters.Empty
    )
}
