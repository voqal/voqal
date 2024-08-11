package dev.voqal.ide.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import dev.voqal.assistant.tool.system.CancelTool
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
import kotlinx.coroutines.launch

open class DisableVoqalAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val configService = project.service<VoqalConfigService>()
        val config = configService.getConfig()
        configService.updateConfig(config.pluginSettings.copy(enabled = false))

        project.scope.launch {
            project.service<VoqalToolService>().blindExecute(CancelTool(false))
            project.service<VoqalDirectiveService>().reset()
            project.service<VoqalConfigService>().resetAiProvider()
            project.service<VoqalConfigService>().resetCachedConfig()
            project.service<VoqalStatusService>().update(VoqalStatus.DISABLED)
        }
    }
}
