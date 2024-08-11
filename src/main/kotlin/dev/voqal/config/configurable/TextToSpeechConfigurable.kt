package dev.voqal.config.configurable

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import dev.voqal.ide.ui.config.TextToSpeechSettingsPanel
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.scope
import kotlinx.coroutines.launch
import javax.swing.JComponent

class TextToSpeechConfigurable(val project: Project) : Configurable {

    private var form: TextToSpeechSettingsPanel? = null

    override fun getDisplayName(): String = "Text-to-Speech"
    override fun isModified(): Boolean = form?.isModified(
        project.service<VoqalConfigService>().getConfig().textToSpeechSettings
    ) == true

    override fun apply() {
        val updateSettings = form!!.getConfig()
        val configService = project.service<VoqalConfigService>()
        configService.updateConfig(updateSettings)

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

            form = TextToSpeechSettingsPanel(project)
            form!!.applyConfig(config.textToSpeechSettings)
        }
        return form
    }

    override fun disposeUIResources() {
        form = null
    }

    override fun reset() {
        val config = project.service<VoqalConfigService>().getConfig()
        form?.applyConfig(config.textToSpeechSettings)
    }
}
