package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject

class UnselectTool : VoqalTool() {

    companion object {
        const val NAME = "unselect"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val updateText = args.getBoolean("updateText", true)
        val editor = directive.ide.editor
        if (editor != null) {
            val hasSelection = ReadAction.compute(ThrowableComputable { editor.selectionModel.hasSelection() })
            if (hasSelection) {
                log.info("Unselecting text")
                ApplicationManager.getApplication().invokeAndWait {
                    editor.selectionModel.removeSelection()
                }
                if (updateText) {
                    project.service<VoqalStatusService>().updateText("Unselect text")
                }
            } else {
                if (updateText) {
                    project.service<VoqalStatusService>().updateText("No text selected")
                }
            }
        } else {
            log.warn("No selected text editor")
            if (updateText) {
                project.service<VoqalStatusService>().updateText("No selected text editor")
            }
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "unselect" -> Pair(NAME, mapOf())
            "and select" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Unselect text",
        parameters = Parameters.Empty
    )
}
