package dev.voqal.assistant.tool.ide.navigation

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalToolService
import io.vertx.core.json.JsonObject

class GotoNextTool : VoqalTool() {

    companion object {
        const val NAME = "goto_next"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        project.service<VoqalToolService>().blindExecute(
            GotoTextTool(), args.put("direction", "next")
        )
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        val memoryService = project.service<VoqalMemoryService>()
        if (memoryService.getLongTermUserData("last_executed_tool") != GotoTextTool.NAME) {
            return null
        }

        return if (transcript.cleanTranscript == "next") {
            val args = memoryService.getLongTermUserData("last_executed_tool_args") as JsonObject
            DetectedIntent(NAME, args.map, transcript, this)
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective) = false
    override fun asTool(directive: VoqalDirective) = throw UnsupportedOperationException("Not supported")
}
