package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout
import javax.swing.JPanel

class ChatToolWindowScrollablePanel : ScrollablePanel(VerticalStackLayout()) {

    private data class DebuggableMessagePanel(
        val panel: JPanel,
        val isDebugMessage: Boolean
    )

    private val messagePanels = mutableListOf<DebuggableMessagePanel>()
    var debugVisible = false

    fun add(component: ChatMessagePanel) {
        messagePanels.add(DebuggableMessagePanel(component, component.isDebug))
        if (!component.isDebug || debugVisible) {
            super.add(component)
        }
    }

    fun clearAll() {
        messagePanels.clear()
        removeAll()
        update()
    }

    fun update() {
        repaint()
        revalidate()
    }

    fun reload() {
        removeAll()
        messagePanels.forEach { (panel, isDebugMessage) ->
            if (!isDebugMessage || debugVisible) {
                add(panel)
            }
        }
        update()
    }
}
