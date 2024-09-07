package dev.voqal.assistant.tool.system.mode

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.CancelTool
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class ToggleEditModeTool : VoqalTool() {

    companion object {
        const val NAME = "toggle_edit_mode"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val configService = project.service<VoqalConfigService>()
        if (!configService.getConfig().pluginSettings.enabled) {
            log.info("Ignoring toggle edit mode. Plugin is disabled")
            return
        }

        var prompt = args.getString("prompt") ?: args.getString("directive")
        when (prompt?.lowercase()) { //ignore default prompts
            "entering edit mode" -> prompt = null
            "entering edit mode." -> prompt = null
            "toggle edit mode" -> prompt = null
            "start voqal edit mode" -> prompt = null
            "enter edit mode" -> prompt = null
            "switch to edit mode" -> prompt = null
            "change to edit mode." -> prompt = null
            "enter edit mode to begin modifying code" -> prompt = null
            "edit mode enabled." -> prompt = null
            "enable edit mode" -> prompt = null
            "edit mode" -> prompt = null
            "edit" -> prompt = null
        }

        val promptSettings = configService.getPromptSettings("Edit Mode")
        val aiProvider = configService.getAiProvider()
        val lmSettings = configService.getLanguageModelSettings(promptSettings)
        if (!aiProvider.isStmProvider() && !aiProvider.isSttProvider() && !directive.developer.chatMessage) {
            log.warn("No transcription AI provider available")
            project.service<VoqalStatusService>().updateText("No transcription AI provider available")
            return
        } else if (!aiProvider.isSttProvider() && !lmSettings.audioModality && !directive.developer.chatMessage) {
            log.warn("Unable to toggle edit mode. Language model ${lmSettings.name} requires speech-to-text provider")
            project.service<VoqalStatusService>().updateText(
                "Unable to toggle edit mode. Language model ${lmSettings.name} requires speech-to-text provider"
            )
            return
        }

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        val statusService = project.service<VoqalStatusService>()
        if (statusService.getCurrentStatus().first == VoqalStatus.EDITING) {
            log.info("Edit mode disabled")

            project.service<VoqalToolService>().blindExecute(CancelTool())
            project.service<VoqalStatusService>().updateText("Edit mode disabled")
        } else {
            log.info("Edit mode enabled")
            project.service<VoqalMemoryService>().resetMemory()
            project.service<VoqalStatusService>().updateText("Edit mode enabled")
            statusService.update(VoqalStatus.EDITING)

            if (prompt != null) {
                log.debug("Entering edit mode with directive: $prompt")
                val editDirective = project.service<VoqalDirectiveService>().asDirective(
                    transcription = SpokenTranscript(prompt, directive.assistant.speechId),
                    textOnly = directive.developer.textOnly,
                    usingAudioModality = directive.assistant.usingAudioModality,
                    chatMessage = directive.developer.chatMessage,
                    "edit mode"
                )
                project.service<VoqalDirectiveService>().executeDirective(editDirective)
            }
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "taco edit mode" -> Pair(NAME, mapOf())
            "edit edit mode" -> Pair(NAME, mapOf())
            "edit mode" -> Pair(NAME, mapOf())
            "edit mdoe" -> Pair(NAME, mapOf())
            "edit omde" -> Pair(NAME, mapOf())
            "edit mod" -> Pair(NAME, mapOf())
            "toggle edit mode" -> Pair(NAME, mapOf())
            "edit" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        return directive.assistant.promptSettings?.promptName?.lowercase() == "idle mode"
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            append("Pass transcription off to Voqal Edit Mode. Use this tool whenever asked to edit the viewing code. ")
            append("That includes adding, removing, or modifying code (e.g. renaming classes, changing variable names, adding functions, writing comments, etc.) ")
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("prompt", JsonObject().apply {
                    put("type", "string")
                    put("description", "The raw transcript of the user's speech")
                })
            })
            put("required", JsonArray().add("prompt"))
        }.toString())
    )
}
