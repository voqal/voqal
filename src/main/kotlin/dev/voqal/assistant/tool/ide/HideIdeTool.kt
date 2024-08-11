package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject
import javax.swing.JFrame

class HideIdeTool : VoqalTool() {

    companion object {
        const val NAME = "hide_ide"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.info("Hiding IDE")
        //minimize the window
        val rootPane = WindowManager.getInstance().getIdeFrame(project)!!.component
        val frame = (rootPane?.parent as? JFrame)
        frame?.state = JFrame.ICONIFIED
        project.service<VoqalStatusService>().updateText("Hide IDE")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString in setOf(
                "hide the ide",
                "hide ide",
                "hi didi",
                "hi, didi",
                "hi, the ide",
                "hide the id"
            )
        ) {
            Pair(NAME, emptyMap())
        } else {
            null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Hide the IDE",
        parameters = Parameters.Empty
    )
}
