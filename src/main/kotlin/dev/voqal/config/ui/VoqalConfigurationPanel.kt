package dev.voqal.config.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import dev.voqal.config.VoqalConfig
import dev.voqal.ide.ui.config.PluginSettingsPanel
import dev.voqal.ide.ui.config.VoiceDetectionSettingsPanel
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.audioCapture
import net.miginfocom.swing.MigLayout
import javax.swing.JPanel

class VoqalConfigurationPanel(val project: Project) : JBPanel<VoqalConfigurationPanel>() {

    private var pluginSettings: PluginSettingsPanel? = null
    private var voiceDetectionSettings: VoiceDetectionSettingsPanel? = null

    init {
        layout = MigLayout("", "[grow,fill]", "[shrink][shrink][shrink][shrink]")

        val pluginSettingsPanel = JPanel().apply {
            layout = MigLayout("", "[grow,fill]", "[shrink]")
        }
        pluginSettings = PluginSettingsPanel(project, project.audioCapture)
        pluginSettingsPanel.add(pluginSettings)
        add(pluginSettingsPanel, "cell 0 0")

        val voiceDetectionSettingsPanel = JPanel().apply {
            layout = MigLayout("", "[grow,fill]", "[shrink]")
        }
        voiceDetectionSettings = VoiceDetectionSettingsPanel(project, project.audioCapture)
        voiceDetectionSettingsPanel.add(voiceDetectionSettings)
        add(voiceDetectionSettingsPanel, "cell 0 1")
    }

    fun isModified(config: VoqalConfig): Boolean {
        if (pluginSettings!!.isModified(config.pluginSettings)) {
            return true
        }
        if (voiceDetectionSettings!!.isModified(config.voiceDetectionSettings)) {
            return true
        }
        return false
    }

    fun getConfig(): VoqalConfig {
        val currentConfig = project.service<VoqalConfigService>().getConfig()
        return VoqalConfig(
            pluginSettings!!.config,
            voiceDetectionSettings!!.config,
            currentConfig.speechToTextSettings,
            currentConfig.languageModelsSettings,
            currentConfig.textToSpeechSettings,
            currentConfig.promptLibrarySettings
        )
    }

    fun applyConfig(config: VoqalConfig) {
        pluginSettings!!.applyConfig(config.pluginSettings)
        voiceDetectionSettings!!.applyConfig(config.voiceDetectionSettings)
    }
}
