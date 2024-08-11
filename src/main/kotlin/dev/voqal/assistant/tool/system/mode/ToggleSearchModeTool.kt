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
import dev.voqal.config.settings.LanguageModelSettings
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class ToggleSearchModeTool : VoqalTool() {

    companion object {
        const val NAME = "toggle_search_mode"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val configService = project.service<VoqalConfigService>()
        if (!configService.getConfig().pluginSettings.enabled) {
            log.info("Ignoring toggle search mode. Plugin is disabled")
            return
        }

        var prompt = args.getString("prompt") ?: args.getString("directive")
        when (prompt?.lowercase()) { //ignore default prompts
            "entering search mode" -> prompt = null
            "entering search mode." -> prompt = null
            "toggle search mode" -> prompt = null
            "start voqal search mode" -> prompt = null
            "enter search mode" -> prompt = null
            "switch to search mode" -> prompt = null
            "change to search mode." -> prompt = null
            "enter search mode to begin modifying code" -> prompt = null
            "search mode enabled." -> prompt = null
            "enable search mode" -> prompt = null
            "search mode" -> prompt = null
            "search" -> prompt = null
        }

        val promptSettings = configService.getPromptSettings("Search Mode")
        val aiProvider = configService.getAiProvider()
        val lmSettings = configService.getLanguageModelSettings(promptSettings)
        if (!aiProvider.isStmProvider() && !aiProvider.isSttProvider() && !directive.developer.chatMessage) {
            log.warn("No transcription AI provider available")
            project.service<VoqalStatusService>().updateText("No transcription AI provider available")
            return
        } else if (!aiProvider.isSttProvider() && !lmSettings.audioModality && !directive.developer.chatMessage) {
            log.warn("Unable to toggle search mode. Language model ${lmSettings.name} requires speech-to-text provider")
            project.service<VoqalStatusService>().updateText(
                "Unable to toggle search mode. Language model ${lmSettings.name} requires speech-to-text provider"
            )
            return
        } else if (lmSettings.provider != LanguageModelSettings.LMProvider.OPENAI) {
            log.warn("Unable to toggle search mode. Language model ${lmSettings.name} requires OpenAI provider")
            project.service<VoqalStatusService>().updateText(
                "Unable to toggle search mode. Language model ${lmSettings.name} requires OpenAI provider"
            )
            return
        }

        val statusService = project.service<VoqalStatusService>()
        if (statusService.getCurrentStatus().first == VoqalStatus.SEARCHING) {
            log.info("Search mode disabled")

            project.service<VoqalToolService>().blindExecute(CancelTool())
            project.service<VoqalStatusService>().updateText("Search mode disabled")
        } else {
            log.info("Search mode enabled")

            //on first search, upload all files to vector store
            if (promptSettings.vectorStoreId == "") {
                project.service<VoqalSearchService>().syncLocalFilesToVectorStore(lmSettings)
            }

            project.service<VoqalStatusService>().updateText("Search mode enabled")
            statusService.update(VoqalStatus.SEARCHING)

            if (prompt != null) {
                log.debug("Entering edit mode with directive: $prompt")
                val editDirective = project.service<VoqalDirectiveService>().asDirective(
                    transcription = SpokenTranscript(prompt, directive.internal.speechId),
                    textOnly = directive.developer.textOnly,
                    usingAudioModality = directive.internal.usingAudioModality,
                    chatMessage = directive.developer.chatMessage,
                    "search mode"
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

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "taco search mode" -> Pair(NAME, mapOf())
            "search search mode" -> Pair(NAME, mapOf())
            "search mode" -> Pair(NAME, mapOf())
            "search mdoe" -> Pair(NAME, mapOf())
            "search omde" -> Pair(NAME, mapOf())
            "search mod" -> Pair(NAME, mapOf())
            "toggle search mode" -> Pair(NAME, mapOf())
            "search" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean = false
    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            append("Pass transcription off to Voqal Search Mode. Use this tool whenever asked to search for code. ")
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
