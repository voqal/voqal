package dev.voqal.ide.actions

import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.services.*
import kotlinx.coroutines.launch
import java.io.IOException

class ViewPromptAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        project.scope.launch {
            val configService = project.service<VoqalConfigService>()
            val toolService = project.service<VoqalToolService>()
            val promptName = configService.getCurrentPromptMode()
            var nopDirective = project.service<VoqalDirectiveService>()
                .asDirective(SpokenTranscript("n/a", null), promptName = promptName)
            nopDirective = nopDirective.copy(
                assistant = nopDirective.assistant.copy(
                    availableActions = toolService.getAvailableTools().values
                )
            )
            val prompt = nopDirective.toMarkdown()

            val scratchFile = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                ScratchRootType.getInstance().createScratchFile(
                    project,
                    "generated-prompt.md", null, prompt
                )
            })
            if (scratchFile != null) {
                WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                    FileEditorManager.getInstance(project).openFile(scratchFile, true)
                })
            } else {
                throw IOException("Failed to create scratch file")
            }
        }
    }
}
