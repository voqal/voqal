package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
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

        val lastArgs = project.service<VoqalMemoryService>()
            .getLongTermUserData("last_executed_tool_args") as JsonObject
        project.service<VoqalToolService>().blindExecute(
            GotoTextTool(), args.mergeIn(lastArgs).put("direction", "next")
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

    override fun isVisible(directive: VoqalDirective): Boolean {
        val memoryService = directive.project.service<VoqalMemoryService>()
        if (memoryService.getLongTermUserData("last_executed_tool") != GotoTextTool.NAME) {
            return false
        }
        return true
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Repeats the last goto operation to the next match.",
        parameters = Parameters.Empty
    )
}
