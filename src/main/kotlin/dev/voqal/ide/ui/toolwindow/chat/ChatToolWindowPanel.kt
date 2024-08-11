package dev.voqal.ide.ui.toolwindow.chat

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer
import com.intellij.util.ui.JBUI.Panels
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JPanel

class ChatToolWindowPanel(
    project: Project,
    parentDisposable: Disposable
) : SimpleToolWindowPanel(true) {

    lateinit var tabPanel: ChatToolWindowTabPanel

    init {
        init(project, parentDisposable)
    }

    private fun init(project: Project, parentDisposable: Disposable) {
        this.tabPanel = ChatToolWindowTabPanel(project)
        val actionToolbarPanel = JPanel(BorderLayout())

        toolbar = actionToolbarPanel
        val notificationContainer = JPanel(BorderLayout())
        notificationContainer.layout = BoxLayout(notificationContainer, BoxLayout.PAGE_AXIS)
        setContent(Panels.simplePanel(tabPanel.content).addToBottom(notificationContainer))

        Disposer.register(parentDisposable, tabPanel)
    }
}
