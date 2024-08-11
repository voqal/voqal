package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject

class NextTabTool : VoqalTool() {

    companion object {
        const val NAME = "next_tab"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        log.info("Switching to next tab")

        project.service<VoqalStatusService>().updateText("Switching to next tab")
        ApplicationManager.getApplication().invokeAndWait {
            rotateTab(project, 1)
        }
    }

    private fun rotateTab(project: Project, direction: Int) {
        val manager = FileEditorManagerEx.getInstanceEx(project)
        val window = manager.currentWindow ?: return
        var index = 0
        // find the current tab index and shift
        for (i in 0 until window.tabCount) {
            val editor = window.editors[i]
            if (editor == window.selectedComposite) {
                index = i
            }
        }
        index += direction
        // switch tab will catch over/underflow
        return switchTab(project, index)
    }

    private fun switchTab(project: Project, index: Int) {
        val manager = FileEditorManagerEx.getInstanceEx(project)
        val window = manager.currentWindow ?: return
        // catch over/underflow
        var newIndex = index
        if (index < 0) {
            newIndex = window.editors.size - 1
        }
        if (index >= window.editors.size) {
            newIndex = 0
        }
        // switch tab
        window.setSelectedEditor(window.editors[newIndex], true)
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "next tab" || rawString == "next step") { //todo: boost words
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun canShortcut(project: Project, call: FunctionCall) = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Switch to the next tab",
        parameters = Parameters.Empty
    )
}
