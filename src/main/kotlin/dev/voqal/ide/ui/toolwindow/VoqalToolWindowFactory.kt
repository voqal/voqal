package dev.voqal.ide.ui.toolwindow

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import dev.voqal.ide.actions.OpenSettingsAction
import dev.voqal.ide.actions.ToggleDebugChatAction
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowPanel
import dev.voqal.services.logsTab

class VoqalToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val chatToolWindowPanel = ChatToolWindowPanel(project, toolWindow.disposable)
        val contentFactory = ContentFactory.getInstance()

        val voqalChat = contentFactory.createContent(chatToolWindowPanel, "Chat", false)
        voqalChat.isCloseable = false
        toolWindow.contentManager.addContent(voqalChat, 0)

        val voqalLogsTab = project.logsTab
        val voqalLogs = contentFactory.createContent(voqalLogsTab.splitter, "Logs", false)
        voqalLogs.isCloseable = false
        toolWindow.contentManager.addContent(voqalLogs, 1)

        val actionList = mutableListOf<AnAction>()
        actionList.add(OpenSettingsAction("Settings"))
        actionList.add(ToggleDebugChatAction("Debug Chat"))
        toolWindow.setTitleActions(actionList)
    }
}
