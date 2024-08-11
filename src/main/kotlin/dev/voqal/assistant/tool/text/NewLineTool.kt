package dev.voqal.assistant.tool.text

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.actionSystem.EditorActionManager
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

class NewLineTool : VoqalTool() {

    companion object {
        const val NAME = "new_line"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.debug("Triggering new line")

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        val actionManager = EditorActionManager.getInstance()
        val actionHandler = actionManager.getActionHandler(
            IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT
        )
        actionHandler.runForAllCarets() //todo: use only carets can find
        WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            actionHandler.execute(
                editor,
                null,
                DataManager.getInstance().dataContext
            )
            project.scope.launch {
                project.service<VoqalStatusService>().updateText("New line added")
            }
        })
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "new line" -> Pair(NAME, mapOf())
            "enter" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Press the new line key.",
        parameters = Parameters.Empty
    )
}
