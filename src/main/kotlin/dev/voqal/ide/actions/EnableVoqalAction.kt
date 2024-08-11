package dev.voqal.ide.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import kotlinx.coroutines.launch

class EnableVoqalAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val configService = project.service<VoqalConfigService>()
        val config = configService.getConfig()
        configService.updateConfig(config.pluginSettings.copy(enabled = true))

        project.scope.launch {
            project.service<VoqalConfigService>().resetAiProvider()
            project.service<VoqalConfigService>().getAiProvider()
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
        }
    }
}
