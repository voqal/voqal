package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class IgnoreTool : VoqalTool() {

    companion object {
        const val NAME = "ignore"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val transcription = args.getString("transcription")
        val ignoreReason = args.getString("ignore_reason")
        project.service<VoqalStatusService>()
            .updateText("Ignoring transcription: $transcription with reason: $ignoreReason")
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "thank you" -> Pair(NAME, mapOf("transcription" to rawString, "ignore_reason" to "IgnoreTool"))
            "thanks for watching" -> Pair(NAME, mapOf("transcription" to rawString, "ignore_reason" to "IgnoreTool"))
            else -> null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Ignore the transcription",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("transcription", JsonObject().apply {
                    put("type", "string")
                    put("description", "The transcription to ignore.")
                })
                put("ignore_reason", JsonObject().apply {
                    put("type", "string")
                    put("description", "The reason for ignoring the transcription.")
                })
            })
            put("required", JsonArray().add("transcription").add("ignore_reason"))
        }.toString())
    )
}
