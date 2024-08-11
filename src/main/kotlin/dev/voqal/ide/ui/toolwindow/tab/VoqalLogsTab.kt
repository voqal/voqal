package dev.voqal.ide.ui.toolwindow.tab

import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.ui.DarculaColors
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.messages.MessageBusConnection
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import java.awt.BorderLayout
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit

class VoqalLogsTab(private val project: Project, messageBusConnection: MessageBusConnection) {

    val splitter: OnePixelSplitter

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS")
    private val emptyLogHtml = buildString {
        append("<html><body ")
        append("id='body' ")
        append("style='font-family: monospace; font-size: smaller;'>")
        append("</body></html>")
    }
    private val logArea = JTextPane().apply {
        isEditable = false
        editorKit = HTMLEditorKit()
        contentType = "text/html"
        text = emptyLogHtml
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground
    }
    private val scrollPane = JBScrollPane(logArea)
    private val panel = JPanel(BorderLayout()).apply {
        scrollPane.border = null
        add(scrollPane, BorderLayout.CENTER)
    }
    var logLevel = "INFO"

    init {
        messageBusConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener {
            if (it != null) {
                SwingUtilities.invokeLater { updateUi(it) }
            }
        })
        splitter = OnePixelSplitter(true, .98f)
        splitter.dividerWidth = 2

        splitter.firstComponent = panel
        splitter.secondComponent = JPanel().apply {
            val levelComboBox = JComboBox(
                arrayOf(
                    "TRACE",
                    "DEBUG",
                    "INFO",
                    "WARN",
                    "ERROR"
                )
            ).apply {
                selectedItem = logLevel
                addActionListener {
                    logLevel = (it.source as JComboBox<*>).selectedItem as String
                }
            }
            add(levelComboBox, BorderLayout.EAST)

            val clearButton = JButton("Clear").apply {
                addActionListener {
                    logArea.text = emptyLogHtml
                }
            }
            add(clearButton, BorderLayout.WEST)
        }
    }

    private fun updateUi(scheme: EditorColorsScheme) {
        logArea.background = scheme.defaultBackground
        logArea.text = emptyLogHtml
    }

    private fun getColorFromScheme(scheme: EditorColorsScheme, key: TextAttributesKey): String {
        val attributes = scheme.getAttributes(key)
        return if (attributes != null) {
            String.format(
                "#%02x%02x%02x",
                attributes.foregroundColor.red,
                attributes.foregroundColor.green,
                attributes.foregroundColor.blue
            )
        } else {
            "#000000"
        }
    }

    fun addLog(millis: Long, level: String, message: String?) {
        val log = project.getVoqalLogger(this::class)
        if (message == null) return
        if (logLevel == "ERROR" && level != "ERROR") return
        if (logLevel == "WARN" && level !in listOf("ERROR", "WARN")) return
        if (logLevel == "INFO" && level !in listOf("ERROR", "WARN", "INFO")) return
        if (logLevel == "DEBUG" && level !in listOf("ERROR", "WARN", "INFO", "DEBUG")) return
        if (logLevel == "TRACE" && level !in listOf("ERROR", "WARN", "INFO", "DEBUG", "TRACE")) return

        val shortTime = timeFormat.format(Date(millis))
        val logDiv = getLogAsHtml(Log(shortTime, level, message))
        project.invokeLater {
            val isScrolledToEnd = scrollPane.verticalScrollBar.value + scrollPane.verticalScrollBar.height ==
                    scrollPane.verticalScrollBar.maximum || !scrollPane.verticalScrollBar.isVisible
            val doc = logArea.document as HTMLDocument
            val elem = doc.getElement("body")
            try {
                doc.insertBeforeEnd(elem, logDiv)
            } catch (ex: Exception) {
                log.warn("Error adding log to VoqalLogsTab", ex)
            }

            if (isScrolledToEnd) {
                scrollPane.validate()
                scrollPane.verticalScrollBar.value = scrollPane.verticalScrollBar.maximum
            }
        }
    }

    private fun getLogAsHtml(
        logEntry: Log,
        scheme: EditorColorsScheme = EditorColorsManager.getInstance().globalScheme
    ): String {
        val levelColor = when (logEntry.level) {
            "ERROR" -> String.format(
                "#%02x%02x%02x",
                DarculaColors.RED.red,
                DarculaColors.RED.green,
                DarculaColors.RED.blue
            )

            "WARN" -> String.format(
                "#%02x%02x%02x",
                DarculaColors.BLUE.red,
                DarculaColors.BLUE.green,
                DarculaColors.BLUE.blue
            )

            else -> getColorFromScheme(scheme, HighlighterColors.TEXT)
        }

        return """
                    <div>
                        <span style="color: gray;">${logEntry.time}</span>
                        <span style="color: $levelColor;">[${logEntry.level}]</span>
                        <span style="color: $levelColor;">${logEntry.message}</span>
                    </div>
                """.trimIndent()
    }

    private data class Log(val time: String, val level: String, val message: String)
}
