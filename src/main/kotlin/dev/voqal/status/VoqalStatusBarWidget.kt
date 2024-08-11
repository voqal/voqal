package dev.voqal.status

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import dev.voqal.config.VoqalConfig
import dev.voqal.config.settings.TextToSpeechSettings
import dev.voqal.ide.VoqalIcons
import dev.voqal.services.*
import dev.voqal.utils.SharedAudioCapture

/**
 * The status bar widget for Voqal in the bottom right corner of the IDE.
 * Provides a visual representation of the current [VoqalStatus].
 */
class VoqalStatusBarWidget(project: Project) : EditorBasedStatusBarPopup(project, false) {

    companion object {
        const val WIDGET_ID = "dev.voqal.widget"

        fun update(project: Project) {
            val widget = findWidget(project)
            widget?.update { widget.myStatusBar?.updateWidget(WIDGET_ID) }
        }

        private fun findWidget(project: Project): VoqalStatusBarWidget? {
            val bar = WindowManager.getInstance().getStatusBar(project)
            if (bar != null) {
                val widget = bar.getWidget(WIDGET_ID)
                if (widget is VoqalStatusBarWidget) {
                    return widget
                }
            }
            return null
        }
    }

    private var activePopup: ListPopup? = null
    private var voqalConfig: VoqalConfig
    private var isDeveloperTalking = false
    private var isAssistantWorking = false

    init {
        val configService = project.service<VoqalConfigService>()
        voqalConfig = configService.getConfig()
        configService.onConfigChange(this) {
            voqalConfig = it

            if (!voqalConfig.pluginSettings.enabled) {
                isDeveloperTalking = false
                isAssistantWorking = false
            }
        }
        project.service<VoqalDirectiveService>().onDirectiveExecution(this) { service, _ ->
            isAssistantWorking = service.isActive()
            project.invokeLater { update(project) }
        }

        val statusService = project.service<VoqalStatusService>()
        project.audioCapture.registerListener(object : SharedAudioCapture.AudioDataListener {
            override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
                if (!isDeveloperTalking && detection.speechDetected.get()) {
                    isDeveloperTalking = true
                    project.invokeLater { update(project) }
                    if (statusService.getStatus() == VoqalStatus.IDLE) {
                        project.service<VoqalVoiceService>().playSound(
                            "notification/listening",
                            TextToSpeechSettings(volume = 2)
                        )
                    }
                } else if (isDeveloperTalking && !detection.speechDetected.get()) {
                    isDeveloperTalking = false
                    project.invokeLater { update(project) }
                }
            }

            override fun isLiveDataListener() = true
        })
    }

    override fun ID(): String = WIDGET_ID

    override fun getWidgetState(file: VirtualFile?): WidgetState {
        val statusAndMessage = project.service<VoqalStatusService>().getCurrentStatus()
        val status = statusAndMessage.first
        val toolTip = statusAndMessage.second ?: status.presentableText
        return WidgetState(toolTip, "", true).apply {
            icon = if (isDeveloperTalking) {
                VoqalIcons.logoListening
            } else if (status == VoqalStatus.IDLE && isAssistantWorking) {
                VoqalIcons.logoProcessing
            } else {
                status.icon
            }
        }
    }

    override fun createPopup(context: DataContext): ListPopup? {
        val configuredGroup = ActionManager.getInstance().getAction(findPopupMenuId())
        return if (configuredGroup !is ActionGroup) {
            null
        } else {
            val statusGroup = DefaultActionGroup()
            statusGroup.addAll(listOf(configuredGroup))

            activePopup = JBPopupFactory.getInstance().createActionGroupPopup(
                "Controls",
                statusGroup,
                context,
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                true
            )
            activePopup
        }
    }

    private fun findPopupMenuId(): String {
        return if (voqalConfig.pluginSettings.enabled) {
            "voqal.enabled.statusBarPopup"
        } else {
            "voqal.disabled.statusBarPopup"
        }
    }

    override fun createInstance(project: Project): StatusBarWidget {
        return VoqalStatusBarWidget(project)
    }
}
