package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.history.core.revisions.Revision
import com.intellij.history.integration.LocalHistoryImpl
import com.intellij.history.integration.ui.models.DirectoryHistoryDialogModel
import com.intellij.history.integration.ui.models.RevisionItem
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.ide.UnselectTool
import dev.voqal.assistant.tool.text.EditTextTool.Companion.VOQAL_HIGHLIGHTERS
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonObject

class CancelTool(private val updateText: Boolean = true) : VoqalTool() {

    companion object {
        const val NAME = "cancel"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.info("Triggering cancel intent")

        val statusService = project.service<VoqalStatusService>()
        if (statusService.getStatus() == VoqalStatus.IDLE) {
            if (updateText) {
                //todo: try unselect before saying cancel unavailable
                project.service<VoqalStatusService>().updateText("Cancel unavailable")
            }
            return
        }

        val memoryService = project.service<VoqalMemoryService>()
        val visibleRangeHighlighter = memoryService.getUserData("visibleRangeHighlighter")
        val memory = memoryService.getCurrentMemory()
        memoryService.resetMemory()

        //do anything considered cancelling
        val editor = directive.ide.editor
        if (statusService.getStatus() == VoqalStatus.SEARCHING) {
            //nop
        } else if (statusService.getStatus() == VoqalStatus.EDITING) {
            val vcs = LocalHistoryImpl.getInstanceImpl().facade
            val gateway = LocalHistoryImpl.getInstanceImpl().gateway
            val baseDir = project.service<VoqalSearchService>().getProjectRoot()
            val memoryId = memory.id
            log.debug("Reverting changes from memory slice: $memoryId")

            val historyModel = DirectoryHistoryDialogModel(project, gateway, vcs, baseDir)
            val currentRevision = historyModel.currentRevision
            val revertRevision = historyModel.revisions.lastOrNull {
                it.labels.any { label -> label.label == "voqal.edit.$memoryId" } ||
                        it.revision.changeSetName == "voqal.edit.$memoryId"
            }
            log.debug("Current revision: $currentRevision - Revert revision: $revertRevision")

            var revisionBeforeRevert: RevisionItem? = null
            historyModel.revisions.forEachIndexed { index, revision ->
                if (revision == revertRevision) {
                    revisionBeforeRevert = historyModel.revisions.getOrNull(index - 1)
                }
            }
            if (revertRevision == null) {
                log.warn("No revision found for memory slice: $memoryId")
            } else {
                log.debug("Reverting to revision: ${revertRevision.revision}")
            }

            val diffs = revisionBeforeRevert?.revision?.let {
                Revision.getDifferencesBetween(it, currentRevision)
            } ?: emptyList()
            if (diffs.isNotEmpty()) {
                project.invokeLater {
                    val reverter = historyModel.createRevisionReverter(diffs)
                    if (reverter == null || reverter.checkCanRevert().isNotEmpty()) {
                        log.warn("Failed to revert changes")
                    } else {
                        reverter.revert()
                        log.info("Changes reverted successfully")
                    }
                }
            } else {
                log.info("Found no changes to revert")
            }
        } else {
            log.warn("Invalid status: ${statusService.getStatus()}")
        }

        //ensure highlighters removed
        if (visibleRangeHighlighter is RangeHighlighter) {
            editor?.markupModel?.removeHighlighter(visibleRangeHighlighter)
        }
        editor?.getUserData(VOQAL_HIGHLIGHTERS)?.forEach {
            editor.markupModel.removeHighlighter(it)
        }
        editor?.putUserData(VOQAL_HIGHLIGHTERS, emptyList())
        project.service<VoqalToolService>().blindExecute(UnselectTool(false))

        //reset status
        project.service<VoqalStatusService>().updateText("Cancelled")

        if (statusService.getStatus() !in setOf(VoqalStatus.DISABLED, VoqalStatus.IDLE)) {
            statusService.update(VoqalStatus.IDLE)
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "cancel") {
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
        description = "Cancel the current action",
        parameters = Parameters.Empty
    )
}
