package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.text.EditTextTool.Companion.VOQAL_HIGHLIGHTERS
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
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
        log.info("Triggering looks good")

        if (project.service<VoqalStatusService>().getStatus() == VoqalStatus.EDITING) {
            log.info("Looks good while editing")
            val memoryService = project.service<VoqalMemoryService>()
            val editRangeHighlighter = memoryService.getUserData("editRangeHighlighter")
            val inlay = memoryService.getUserData("voqal.edit.inlay") as Inlay<*>?
            val editor = (memoryService.getUserData("voqal.edit.editor") as Editor?) ?: directive.ide.editor
            memoryService.resetMemory()

            inlay?.let { project.invokeLater { Disposer.dispose(it) } }

            //ensure highlighters removed
            if (editRangeHighlighter is RangeHighlighter) {
                if (editor?.virtualFile != null) {
                    editor.markupModel.removeHighlighter(editRangeHighlighter)
                }
            }
            val voqalHighlighters = editor?.getUserData(VOQAL_HIGHLIGHTERS) ?: emptyList()
            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                voqalHighlighters.forEach {
                    editor?.markupModel?.removeHighlighter(it)
                }
            })
            editor?.putUserData(VOQAL_HIGHLIGHTERS, emptyList())

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

    internal fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return when (rawString) {
            "looks good" -> Pair(NAME, mapOf())
            "it looks good" -> Pair(NAME, mapOf())
            else -> null
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        return directive.assistant.promptSettings?.promptName?.lowercase() != "idle mode"
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Looks good to me a.k.a. accept the changes",
        parameters = Parameters.Empty
    )
}
