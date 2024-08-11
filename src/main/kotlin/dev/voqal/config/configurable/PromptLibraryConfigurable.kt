package dev.voqal.config.configurable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import dev.voqal.ide.ui.config.PromptLibrarySettingsPanel
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.scope
import kotlinx.coroutines.launch
import javax.swing.JComponent

class PromptLibraryConfigurable(val project: Project) : Configurable {

    private var form: PromptLibrarySettingsPanel? = null

    override fun getDisplayName(): String = "Prompt Library"
    override fun isModified(): Boolean = form?.isModified(
        project.service<VoqalConfigService>().getConfig().promptLibrarySettings
    ) == true

    override fun apply() {
        val updatedSettings = form!!.getConfig()
        val configService = project.service<VoqalConfigService>()
        configService.updateConfig(updatedSettings)

        project.scope.launch {
            //pre-initialize AI provider
            project.service<VoqalConfigService>().getAiProvider()
        }
    }

    override fun createComponent(): JComponent? {
        if (form == null && !ApplicationManager.getApplication().isHeadlessEnvironment) {
            val config = project.service<VoqalConfigService>().getConfig()

            form = PromptLibrarySettingsPanel(project)
            form!!.applyConfig(config.promptLibrarySettings)
        }
        return form
    }

    override fun disposeUIResources() {
        form = null
    }

    override fun reset() {
        val config = project.service<VoqalConfigService>().getConfig()
        form?.applyConfig(config.promptLibrarySettings)
    }
}
