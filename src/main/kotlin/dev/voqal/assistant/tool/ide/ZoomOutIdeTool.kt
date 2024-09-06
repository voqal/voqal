package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.VoqalToolService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject

class ZoomOutIdeTool : VoqalTool() {

    companion object {
        const val NAME = "zoom_out_ide"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.debug("Zooming out IDE")
        project.service<VoqalToolService>().executeAnAction(
            emptyMap(),
            ActionManager.getInstance().getAction("ZoomOutIdeAction")
        )
        project.service<VoqalStatusService>().updateText("Zoomed out IDE")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "zoom out ide") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Zoom out IDE",
        parameters = Parameters.Empty
    )
}
