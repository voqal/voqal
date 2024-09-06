package dev.voqal.assistant.tool.text

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject
import java.awt.Robot
import java.awt.event.KeyEvent.VK_BACK_SPACE

class BackspaceTool : VoqalTool() {

    companion object {
        const val NAME = "backspace"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.debug("Triggering backspace")

        //todo: not robot
        Robot().keyPress(VK_BACK_SPACE)
        Robot().keyRelease(VK_BACK_SPACE)
        project.service<VoqalStatusService>().updateText("Backspace")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "backspace") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Press the backspace key.",
        parameters = Parameters.Empty
    )
}
