package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

class RedoTool : VoqalTool() {

    companion object {
        const val NAME = "redo"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project

        //todo: get from event?
        val fileEditor = FileEditorManager.getInstance(project).selectedEditor
        ApplicationManager.getApplication().invokeAndWait {
            if (UndoManager.getInstance(project).isRedoAvailable(fileEditor)) {
                UndoManager.getInstance(project).redo(fileEditor)
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("Redo")
                }
            } else {
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("No redo available")
                }
            }
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "redo") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Redo the last action",
        parameters = Parameters.Empty
    )
}
