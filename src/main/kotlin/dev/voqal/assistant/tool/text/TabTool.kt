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
import java.awt.event.KeyEvent.VK_TAB

class TabTool : VoqalTool() {

    companion object {
        const val NAME = "tab"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.debug("Triggering tab")

        //todo: not robot
        Robot().keyPress(VK_TAB)
        Robot().keyRelease(VK_TAB)
        project.service<VoqalStatusService>().updateText("Tab")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "tab" -> Pair(NAME, mapOf())
            "tap" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Press the tab key.",
        parameters = Parameters.Empty
    )
}
