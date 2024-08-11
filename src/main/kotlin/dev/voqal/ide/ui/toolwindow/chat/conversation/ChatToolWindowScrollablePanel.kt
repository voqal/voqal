package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class ChatToolWindowScrollablePanel : ScrollablePanel(VerticalStackLayout()) {

    private data class Test(
        val panel: JPanel,
        val isDebugMessage: Boolean
    )

    private val allMessagePanels: MutableList<Test> = mutableListOf()
    var debugVisible = false

    fun displayLandingView(landingView: JComponent?) {
        clearAll()
        add(landingView)
    }

    fun add(component: ChatMessagePanel) {
        allMessagePanels.add(Test(component, component.isDebug))
        if (!component.isDebug || debugVisible) {
            super.add(component)
        }
    }

    fun addMessage(debugMessage: Boolean): JPanel {
        val messageWrapper = JPanel()
        messageWrapper.layout = BoxLayout(messageWrapper, BoxLayout.PAGE_AXIS)
        allMessagePanels.add(Test(messageWrapper, debugMessage))

        if (!debugMessage || debugVisible) {
            add(messageWrapper)
        }
        return messageWrapper
    }

    fun clearAll() {
        allMessagePanels.clear()
        removeAll()
        update()
    }

    fun update() {
        repaint()
        revalidate()
    }

    fun reload() {
        removeAll()
        allMessagePanels.forEach { (panel, isDebugMessage) ->
            if (!isDebugMessage || debugVisible) {
                add(panel)
            }
        }
        update()
    }
}
