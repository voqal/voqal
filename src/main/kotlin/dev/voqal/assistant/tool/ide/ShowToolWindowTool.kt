package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class ShowToolWindowTool : VoqalTool() {

    companion object {
        const val NAME = "show_tool_window"

        //todo: load dynamically
        val TOOL_WINDOW_TYPES = listOf(
            "problems", "project", "endpoints",
            "debug", "run", "commit",
            "structure", "vocal", "build",
            "git", "version control", "terminal",
            "services"
        )
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        var toolWindowType = args.getString("toolWindowType")?.toString()?.capitalize()
        if (toolWindowType == null) {
            var shortcutDirective = args.getString("directive")
            if (shortcutDirective == "voqal") {
                shortcutDirective = "vocal"
            }
            toolWindowType = if (shortcutDirective in TOOL_WINDOW_TYPES) {
                shortcutDirective
            } else {
                attemptIntentExtract(shortcutDirective)?.second?.get("toolWindowType")
            }.toString().capitalize()
        }
        if (toolWindowType == "Vocal") {
            toolWindowType = "Voqal"
        } else if (toolWindowType == "Problems") {
            toolWindowType = "Problems View"
        } else if (toolWindowType == "Git") {
            toolWindowType = "Version Control"
        } else if (toolWindowType == "Version control") {
            toolWindowType = "Version Control"
        }
        log.info("Showing tool window: $toolWindowType")

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(toolWindowType)
        if (toolWindow != null) {
            ApplicationManager.getApplication().invokeAndWait {
                toolWindow.show()
            }
            project.service<VoqalStatusService>().updateText("Show tool window $toolWindowType")
        } else {
            log.warn("Tool window '$toolWindowType' not found")
            project.service<VoqalStatusService>().updateText("Tool window '$toolWindowType' not found")
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        if (rawString.startsWith("show") || rawString.startsWith("open")) {
            val toolWindowType = rawString.substringAfter("show ").substringAfter("open ")
            if (TOOL_WINDOW_TYPES.contains(toolWindowType.lowercase())) {
                return Pair(NAME, mapOf("toolWindowType" to toolWindowType))
            } else if (toolWindowType.lowercase() == "voqal") {
                return Pair(NAME, mapOf("toolWindowType" to "vocal"))
            }
        }

        return when (rawString) {
            "show get" -> Pair(NAME, mapOf("toolWindowType" to "git"))
            "showkit" -> Pair(NAME, mapOf("toolWindowType" to "git"))
            else -> null //todo: show_voqal (shortcut)
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall): Boolean {
        val directive: String?
        try {
            directive = JsonObject(call.arguments).getString("directive")!!
        } catch (_: Exception) {
            return false
        }
        if (directive == "voqal") return true

        return when {
            directive in TOOL_WINDOW_TYPES -> true
            attemptIntentExtract(directive) != null -> true
            else -> false
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Show the tool window (e.g. show voqal, show project)",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("toolWindowType", JsonObject().apply {
                    put("type", "string")
                    put("description", "The tool window type to show")
                    put("enum", JsonArray(TOOL_WINDOW_TYPES))
                })
            })
            put("required", JsonArray().add("toolWindowType"))
        }.toString())
    )
}
