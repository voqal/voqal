package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.text.EditTextTool.Companion.VOQAL_HIGHLIGHTERS
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonObject

class LooksGoodTool : VoqalTool() {

    companion object {
        const val NAME = "looks_good"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.info("Triggering looks good intent")

        val editor = directive.ide.editor
        if (editor == null) {
            log.warn("No editor found")
            project.service<VoqalStatusService>().updateText("No editor found")
            return
        }

        if (project.service<VoqalStatusService>().getStatus() == VoqalStatus.EDITING) {
            log.info("Looks good while editing")
            val memoryService = project.service<VoqalMemoryService>()
            val visibleRangeHighlighter = memoryService.getUserData("visibleRangeHighlighter")
            memoryService.resetMemory()

            //ensure highlighters removed
            if (visibleRangeHighlighter is RangeHighlighter) {
                editor.markupModel.removeHighlighter(visibleRangeHighlighter)
            }
            val voqalHighlighters = editor.getUserData(VOQAL_HIGHLIGHTERS) ?: emptyList()
            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                voqalHighlighters.forEach {
                    editor.markupModel.removeHighlighter(it)
                }
            })
            editor.putUserData(VOQAL_HIGHLIGHTERS, emptyList())

            //reset status
            project.service<VoqalStatusService>().updateText("Accepted changes")
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
        } else {
            project.service<VoqalStatusService>().updateText("No action to take")
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "looks good") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        return directive.internal.promptSettings?.promptName?.lowercase() != "idle mode"
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Looks good to me a.k.a. accept the changes",
        parameters = Parameters.Empty
    )
}