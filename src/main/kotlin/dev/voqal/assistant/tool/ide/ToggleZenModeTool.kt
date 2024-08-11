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

class ToggleZenModeTool : VoqalTool() {

    companion object {
        const val NAME = "toggle_zen_mode"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.info("Toggling Zen mode")

        val toggleZenModeAction = ActionManager.getInstance().getAction("ToggleZenMode")
        project.service<VoqalToolService>().executeAnAction(emptyMap(), toggleZenModeAction)
        project.service<VoqalStatusService>().updateText("Toggled Zen mode")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "toggle zen mode" -> Pair(NAME, mapOf())
            "toggles zen mode" -> Pair(NAME, mapOf())
            "zen mode" -> Pair(NAME, mapOf())
            "toggles send mode" -> Pair(NAME, mapOf())
            "send mode" -> Pair(NAME, mapOf())
            "zemo" -> Pair(NAME, mapOf())
            "zyn mode" -> Pair(NAME, mapOf())
            "taco zen mode" -> Pair(NAME, mapOf())
            "toggle zone mode" -> Pair(NAME, mapOf())
            "tackled zembro" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Toggles the Zen mode",
        parameters = Parameters.Empty
    )
}
