package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.execution.ExecutionManager
import com.intellij.openapi.components.service
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class StopProgramTool : VoqalTool() {

    companion object {
        const val NAME = "stop_program"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        log.info("Stop program action")
        ExecutionManager.getInstance(project).getRunningProcesses().forEach {
            log.info("Destroying: $it")
            it.destroyProcess()
        }
        project.service<VoqalStatusService>().updateText("Tool $name")
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        //todo: check if any programs running
        // might want to leave reason why not visible? so voqal just doesn't say unknown command
        return super.isVisible(directive)
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Stops all executing programs.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("reason", JsonObject().apply {
                    put("type", "string")
                    put("description", "The reason for stopping executing programs.")
                })
            })
            put("required", JsonArray().add("reason"))
        }.toString())
    )
}
