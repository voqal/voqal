package dev.voqal.services

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.ThreadingAssertions
import dev.voqal.assistant.VoqalResponse
import dev.voqal.config.settings.TextToSpeechSettings
import dev.voqal.ide.actions.ShowQuickEditAction
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowContentManager
import dev.voqal.status.VoqalStatus
import dev.voqal.status.VoqalStatus.*
import dev.voqal.status.VoqalStatusBarWidget
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Keeps track of Voqal's current [VoqalStatus] and notifies listeners when it changes.
 */
@Service(Service.Level.PROJECT)
class VoqalStatusService(private val project: Project) {

    private val log = project.getVoqalLogger(this::class)
    private val statusLock = Any()
    private var status: VoqalStatus = DISABLED
    private var message: String? = null
    private val listeners: MutableList<(VoqalStatus, String?) -> Unit> = CopyOnWriteArrayList()

    init {
        val config = project.service<VoqalConfigService>().getConfig()
        if (config.pluginSettings.enabled) {
            log.debug("Setting initial status to idle")
            update(IDLE)
        }
    }

    fun getCurrentStatus(): Pair<VoqalStatus, String?> {
        ThreadingAssertions.assertBackgroundThread()
        synchronized(statusLock) {
            return Pair(status, message)
        }
    }

    fun getStatus(): VoqalStatus {
        return status
    }

    fun update(status: VoqalStatus, message: String? = null) {
        ThreadingAssertions.assertBackgroundThread()
        synchronized(statusLock) {
            val oldStatus = this.status
            message?.let {
                if (status == ERROR) {
                    log.warn(it)
                } else {
                    log.info(it)
                }
            }
            this.message = message
            if (oldStatus != status) {
                this.status = status
                log.info("Status changed from $oldStatus to $status")

                when (status) {
                    IDLE -> playIdleNotification(oldStatus)
                    ERROR -> notifyError(project, message ?: "An unknown error occurred")
                    else -> Unit
                }
                updateAllStatusBarIcons()

                listeners.toList().forEach {
                    it.invoke(this.status, this.message)
                }
            } else {
                log.warn("Duplicate status change from $oldStatus to $status")
            }
        }
    }

    private fun playIdleNotification(oldStatus: VoqalStatus) {
        if (oldStatus !in setOf(EDITING)) return
        var nextIdleToPlay = 1
        if (oldStatus == EDITING) {
            nextIdleToPlay = 3
        }
        project.service<VoqalVoiceService>().playSound(
            "notification/idle$nextIdleToPlay",
            TextToSpeechSettings(volume = 5)
        )
    }

    fun onStatusChange(
        disposable: Disposable = project.service<ProjectScopedService>(),
        listener: (VoqalStatus, String?) -> Unit
    ) {
        listeners.add(listener)
        log.trace("Added status listener. Available status listeners: " + listeners.size)

        Disposer.register(disposable) {
            listeners.remove(listener)
            log.trace("Removed status listener. Available status listeners: " + listeners.size)
        }
    }

    private fun updateAllStatusBarIcons() {
        project.invokeLater {
            if (!project.isDisposed) {
                VoqalStatusBarWidget.update(project)
            }
        }
    }

    private fun notifyError(project: Project?, content: String) {
        if (System.getProperty("VQL_TEST_MODE") == "true") {
            return
        }
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Voqal Notification")
            .createNotification(content, NotificationType.ERROR)
            .notify(project)
    }

    fun updateText(input: String, response: VoqalResponse? = null) {
        if (System.getProperty("VQL_TEST_MODE") == "true") {
            return
        }
        ThreadingAssertions.assertBackgroundThread()
        log.debug("Updating text: $input")
        project.service<ChatToolWindowContentManager>().addResponse(input, response)
    }

    fun warnChat(input: String) {
        if (System.getProperty("VQL_TEST_MODE") == "true") {
            return
        }
        ThreadingAssertions.assertBackgroundThread()
        project.service<ChatToolWindowContentManager>().addErrorMessage(input)

        if (project.service<VoqalStatusService>().getStatus() == EDITING) {
            val inlay = project.service<VoqalMemoryService>().getUserData("voqal.edit.inlay") as Inlay<*>?
            if (inlay != null) {
                val errorInlay = project.service<VoqalMemoryService>()
                    .getUserData("voqal.edit.inlay.error") as Inlay<*>?
                if (errorInlay == null) {
                    project.invokeLater {
                        ShowQuickEditAction.showErrorInlay(project, inlay.editor, inlay.offset, input, inlay)
                    }
                } else {
                    val errorRenderer = errorInlay.renderer as ShowQuickEditAction.ErrorInlayRenderer
                    errorRenderer.text = input
                    project.invokeLater {
                        errorInlay.update()
                    }
                }
            }
        }
    }
}
