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
        val log = project.getVoqalLogger(this::class)
        project.scope.launch {
            val configService = project.service<VoqalConfigService>()
            val toolService = project.service<VoqalToolService>()
            val promptName = configService.getCurrentPromptMode()
            val promptSettings = configService.getPromptSettings(promptName)
            val models = configService.getConfig().languageModelsSettings.models
            val languageModelSettings = models.firstOrNull {
                it.name == promptSettings.modelName
            } ?: models.firstOrNull()
            if (languageModelSettings == null) {
                log.warn("No language model found for: $promptName")
                project.service<VoqalStatusService>().updateText("No language model found for: $promptName")
                return@launch
            }

            var command = project.service<VoqalDirectiveService>()
                .asDirective(SpokenTranscript("n/a", null), promptName = promptName)
            command = command.copy(
                internal = command.internal.copy(
                    availableActions = toolService.getAvailableTools().values
                )
            )
            val systemPrompt = command.toMarkdown()

            val scratchFile = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                ScratchRootType.getInstance().createScratchFile(
                    project,
                    "generated-prompt.md", null, systemPrompt
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
