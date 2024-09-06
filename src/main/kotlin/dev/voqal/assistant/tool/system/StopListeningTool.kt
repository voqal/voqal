package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.ide.actions.DisableVoqalAction
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.VoqalToolService
import io.vertx.core.json.JsonObject

class StopListeningTool : VoqalTool() {

    companion object {
        const val NAME = "stop_listening"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        project.service<VoqalToolService>().executeAnAction(emptyMap(), DisableVoqalAction())
        project.service<VoqalStatusService>().updateText("Stopped listening")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "stop listening") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Stop listening to the developer completely",
        parameters = Parameters.Empty
    )
}
