package dev.voqal.ide.ui.toolwindow.tab

import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.ui.DarculaColors
import com.intellij.ui.OnePixelSplitter
import dev.voqal.ide.ui.VoqalUI
import dev.voqal.services.ProjectScopedService
import dev.voqal.services.invokeLater
import dev.voqal.services.messageBusConnection
import java.awt.BorderLayout
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class VoqalLogsTab(private val project: Project) {

    companion object {
        private val LOG_LEVEL = Key.create<String>("VOQAL_LOG_LEVEL")
    }

    var logLevel = "INFO"
    val splitter = OnePixelSplitter(true, .98f)

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS")
    private lateinit var logEditor: Editor

    init {
        project.invokeLater {
            initUi()
        }
    }

    private fun initUi() {
        if (System.getProperty("VQL_TEST_MODE") == "true") return
        logEditor = VoqalUI.createPreviewComponent(project, "", false, project.service<ProjectScopedService>())
        logEditor.settings.isLineNumbersShown = false
        logEditor.settings.isRightMarginShown = false

        project.messageBusConnection.subscribe(UISettingsListener.TOPIC, UISettingsListener {
            //make font 30% smaller
            logEditor.colorsScheme.editorFontSize =
                (EditorColorsManager.getInstance().globalScheme.editorFontSize * 0.7 * it.ideScale).toInt()
        })
        project.messageBusConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener {
            //update log level colors on theme switch
            project.invokeLater {
                logEditor.markupModel.allHighlighters.forEach {
                    it.getTextAttributes(null)?.foregroundColor = getTextColor(it.getUserData(LOG_LEVEL)!!)
                }
            }
        })

        splitter.setResizeEnabled(false)
        splitter.dividerWidth = 2

        //todo: seems odd but gets rid of gutter (also need left line border)
        val scrollPane = (logEditor as EditorImpl).scrollPane
        scrollPane.setRowHeaderView(null)
        logEditor.contentComponent.border = EmptyBorder(0, 6, 0, 0)

        splitter.firstComponent = logEditor.component
        splitter.secondComponent = JPanel().apply {
            val levelComboBox = JComboBox(arrayOf("TRACE", "DEBUG", "INFO", "WARN", "ERROR")).apply {
                selectedItem = logLevel
                addActionListener {
                    logLevel = (it.source as JComboBox<*>).selectedItem as String
                }
            }
            add(levelComboBox, BorderLayout.EAST)

            val clearButton = JButton("Clear").apply {
                addActionListener {
                    WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                        logEditor.markupModel.removeAllHighlighters()
                        logEditor.document.setText("")
                    })
                }
            }
            add(clearButton, BorderLayout.WEST)
        }
    }

    fun addLog(millis: Long, level: String, message: String?) {
        if (System.getProperty("VQL_TEST_MODE") == "true") return
        if (message == null) return
        if (logLevel == "ERROR" && level != "ERROR") return
        if (logLevel == "WARN" && level !in listOf("ERROR", "WARN")) return
        if (logLevel == "INFO" && level !in listOf("ERROR", "WARN", "INFO")) return
        if (logLevel == "DEBUG" && level !in listOf("ERROR", "WARN", "INFO", "DEBUG")) return
        if (logLevel == "TRACE" && level !in listOf("ERROR", "WARN", "INFO", "DEBUG", "TRACE")) return

        project.invokeLater {
            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                val textColor = getTextColor(level)
                val shortTime = timeFormat.format(Date(millis))
                insertText("$shortTime [$level] $message\n", textColor, level)

                //todo: stick scroll instead of force scroll
                logEditor.scrollingModel.scrollVertically(Integer.MAX_VALUE)
            })
        }
    }

    private fun insertText(logDiv: String, color: Color, level: String) {
        val len = logEditor.document.textLength
        logEditor.document.insertString(len, logDiv)
        logEditor.markupModel.addRangeHighlighter(
            len, len + logDiv.length,
            HighlighterLayer.LAST,
            TextAttributes().apply { foregroundColor = color },
            HighlighterTargetArea.EXACT_RANGE
        ).apply {
            putUserData(LOG_LEVEL, level)
        }
    }

    private fun getTextColor(level: String): Color {
        val textColor = when (level) {
            "ERROR" -> DarculaColors.RED
            "WARN" -> DarculaColors.BLUE
            else -> EditorColorsManager.getInstance().globalScheme.getAttributes(HighlighterColors.TEXT)!!.foregroundColor
        }
        return textColor
    }
}
