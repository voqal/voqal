package dev.voqal.config.configurable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import dev.voqal.config.ui.VoqalConfigurationPanel
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.audioCapture
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import kotlinx.coroutines.launch
import javax.swing.JComponent

class VoqalConfigurable(val project: Project) : Configurable {

    private var form: VoqalConfigurationPanel? = null

    override fun getDisplayName(): String = "Voqal"
    override fun isModified(): Boolean = form?.isModified(project.service<VoqalConfigService>().getConfig()) == true

    override fun apply() {
        val updatedConfig = form!!.getConfig()
        val configService = project.service<VoqalConfigService>()
        val oldConfig = configService.getConfig()
        configService.updateConfig(updatedConfig.pluginSettings)
        configService.updateConfig(updatedConfig.voiceDetectionSettings)

        project.scope.launch {
            //todo: can do onConfigChange instead of resetAiProvider/getAiProvider
            configService.resetAiProvider()

            val status = project.service<VoqalStatusService>().getCurrentStatus().first
            if (status == VoqalStatus.ERROR && updatedConfig.pluginSettings.enabled) {
                //error -> enabled
                project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            } else if (!oldConfig.pluginSettings.enabled && updatedConfig.pluginSettings.enabled) {
                //disabled -> enabled
                project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            } else if (oldConfig.pluginSettings.enabled && !updatedConfig.pluginSettings.enabled) {
                //enabled -> disabled
                project.service<VoqalStatusService>().update(VoqalStatus.DISABLED)
            }

            //pre-initialize AI provider
            project.service<VoqalConfigService>().getAiProvider()
        }
    }

    override fun createComponent(): JComponent? {
        if (form == null && !ApplicationManager.getApplication().isHeadlessEnvironment) {
            val config = project.service<VoqalConfigService>().getConfig()
            form = VoqalConfigurationPanel(project)
            form!!.applyConfig(config)

            //pause audio capture while the configuration panel is open
            project.audioCapture.pause()
        }
        return form
    }

    override fun disposeUIResources() {
        if (form != null) {
            form = null

            //resume audio capture after the configuration panel is closed
            project.audioCapture.resume()
        }
    }

    override fun reset() {
        val config = project.service<VoqalConfigService>().getConfig()
        form?.applyConfig(config)
    }
}
