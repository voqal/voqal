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
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

class DeleteTool : VoqalTool() {

    companion object {
        const val NAME = "delete"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.debug("Triggering delete")

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        WriteCommandAction.runWriteCommandAction(editor.project, ThrowableComputable {
            if (editor.selectionModel.hasSelection()) {
                val selectionModel = editor.selectionModel
                val start = selectionModel.selectionStart
                val end = selectionModel.selectionEnd
                editor.document.deleteString(start, end)
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("Deleted selection")
                }
            } else {
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("No selection")
                }
            }
        })
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "delete") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Delete the selected text",
        parameters = Parameters.Empty
    )
}
