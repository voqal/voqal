package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.ui.JBUI
import dev.voqal.ide.ui.VoqalUI
import dev.voqal.ide.ui.VoqalUI.handleHyperlinkClicked
import java.awt.BorderLayout
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class ChatMessageResponseBody(
    private val project: Project
) : JPanel(BorderLayout()) {
    private var currentlyProcessedTextPane: JTextPane? = null
    private var responseReceived = false

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        isOpaque = false

        prepareProcessingText()
        currentlyProcessedTextPane!!.text = "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>"
    }

    fun update(partialMessage: String) {
        processResponse(partialMessage)
    }

    fun clear() {
        removeAll()

        prepareProcessingText()
        currentlyProcessedTextPane!!.text = "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>"

        repaint()
        revalidate()
    }

    private fun processResponse(markdownInput: String) {
        responseReceived = true
        processText(markdownInput)
    }

    private fun processText(text: String) {
        if (currentlyProcessedTextPane == null) {
            prepareProcessingText()
        }
        currentlyProcessedTextPane?.setText(text)
    }

    private fun prepareProcessingText() {
        currentlyProcessedTextPane = createTextPane("")
        add(currentlyProcessedTextPane)
    }

    private fun createTextPane(text: String): JTextPane {
        val textPane = VoqalUI.createTextPane(text, false) { event: HyperlinkEvent ->
            if (FileUtil.exists(event.description) && HyperlinkEvent.EventType.ACTIVATED == event.eventType) {
                val file = LocalFileSystem.getInstance().findFileByPath(event.description)
                Objects.requireNonNull(file)?.let { FileEditorManager.getInstance(project).openFile(it, true) }
                return@createTextPane
            }
            handleHyperlinkClicked(event)
        }

        textPane.border = JBUI.Borders.empty()
        return textPane
    }
}
