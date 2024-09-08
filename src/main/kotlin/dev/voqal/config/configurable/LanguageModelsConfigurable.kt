package dev.voqal.config.configurable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import dev.voqal.ide.ui.config.LanguageModelsPanel
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.scope
import kotlinx.coroutines.launch
import javax.swing.JComponent

class LanguageModelsConfigurable(val project: Project) : Configurable {

    private var form: LanguageModelsPanel? = null

    override fun getDisplayName(): String = "Language Model"
    override fun isModified(): Boolean = form?.isModified(
        project.service<VoqalConfigService>().getConfig().languageModelsSettings
    ) == true

    override fun apply() {
        val updatedSettings = form!!.getConfig()
        val configService = project.service<VoqalConfigService>()
        val oldConfig = configService.getConfig()
        configService.updateConfig(updatedSettings)
        form!!.reset(updatedSettings)

        //set prompt library defaults on first language model added
        if (oldConfig.languageModelsSettings.models.isEmpty() && updatedSettings.models.isNotEmpty()) {
            val languageModelName = updatedSettings.models.first().name
            val promptLibrarySettings = configService.getConfig().promptLibrarySettings
            configService.updateConfig(promptLibrarySettings.copy(prompts = promptLibrarySettings.prompts.map {
                it.copy(modelName = languageModelName)
            }))
        }

        project.scope.launch {
            //todo: can do onConfigChange instead of resetAiProvider/getAiProvider
            configService.resetAiProvider()

            //pre-initialize AI provider
            project.service<VoqalConfigService>().getAiProvider()
        }
    }

    override fun createComponent(): JComponent? {
        if (form == null && !ApplicationManager.getApplication().isHeadlessEnvironment) {
            val config = project.service<VoqalConfigService>().getConfig()

            form = LanguageModelsPanel(project)
            form!!.applyConfig(config.languageModelsSettings)
        }
        return form?.createComponent()
    }

    override fun disposeUIResources() {
        form = null
    }

    override fun reset() {
        val config = project.service<VoqalConfigService>().getConfig()
        form?.applyConfig(config.languageModelsSettings)
    }
}
