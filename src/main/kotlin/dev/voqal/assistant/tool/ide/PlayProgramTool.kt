package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.xdebugger.XDebuggerManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import dev.voqal.services.scope
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.launch

class PlayProgramTool : VoqalTool() {

    companion object {
        const val NAME = "play_program"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        log.info("Play program action")
        val promise = Promise.promise<Void>()
        val debugSessions = XDebuggerManager.getInstance(project).debugSessions
        if (debugSessions.size == 1) {
            val session = debugSessions.first()
            project.invokeLater {
                session.resume()
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("Tool $name")
                    promise.complete()
                }
            }
        } else {
            println("todo")
            project.service<VoqalStatusService>().updateText("Tool $name")
            promise.complete()
        }
        promise.future().coAwait()
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        //todo: check if any programs running
        // might want to leave reason why not visible? so voqal just doesn't say unknown command
        return super.isVisible(directive)
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Continue executing currently debugged program.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("reason", JsonObject().apply {
                    put("type", "string")
                    put("description", "The reason for continue to executing program.")
                })
            })
            put("required", JsonArray().add("reason"))
        }.toString())
    )
}
