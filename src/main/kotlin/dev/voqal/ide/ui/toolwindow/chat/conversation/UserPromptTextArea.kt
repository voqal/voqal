package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.CurrentTheme
import com.intellij.util.ui.UIUtil
import dev.voqal.ide.VoqalIcons
import dev.voqal.ide.ui.VoqalUI.addShiftEnterInputMap
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.function.Consumer
import javax.swing.AbstractAction
import javax.swing.JPanel
import javax.swing.UIManager

class UserPromptTextArea(
    private val onSubmit: Consumer<String>,
) : JPanel(BorderLayout()) {

    private val textArea = JBTextArea()
    private val textAreaRadius = 16
    private var stopButton: IconActionButton? = null
    private var submitEnabled = true

    init {
        textArea.isOpaque = false
        textArea.background = BACKGROUND_COLOR
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.emptyText.setText("Type your message here")
        textArea.border = JBUI.Borders.empty(4, 4)
        addShiftEnterInputMap(textArea, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                handleSubmit()
            }
        })
        textArea.addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent) {
                super@UserPromptTextArea.paintBorder(super@UserPromptTextArea.getGraphics())
            }

            override fun focusLost(e: FocusEvent) {
                super@UserPromptTextArea.paintBorder(super@UserPromptTextArea.getGraphics())
            }
        })
        updateFont()
        init()
    }

    val text: String
        get() = textArea.text.trim { it <= ' ' }

    fun focus() {
        textArea.requestFocus()
        textArea.requestFocusInWindow()
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = background
        g2.fillRoundRect(0, 0, width - 1, height - 1, textAreaRadius, textAreaRadius)
        super.paintComponent(g)
    }

    override fun paintBorder(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = CurrentTheme.ActionButton.focusedBorder()
        if (textArea.isFocusOwner) {
            g2.stroke = BasicStroke(1.5f)
        }
        g2.drawRoundRect(0, 0, width - 1, height - 1, textAreaRadius, textAreaRadius)
    }

    override fun getInsets(): Insets {
        return JBUI.insets(6, 12, 6, 6)
    }

    private fun handleSubmit() {
        if (submitEnabled && !textArea.text.isEmpty()) {
            onSubmit.accept(text.trim { it <= ' ' })
            textArea.text = ""
        }
    }

    private fun init() {
        isOpaque = false
        add(textArea, BorderLayout.CENTER)

        stopButton = IconActionButton(
            object : AnAction("Stop", "Stop current inference", AllIcons.Actions.Suspend) {
                override fun actionPerformed(e: AnActionEvent) {
                }
            })
        stopButton!!.isEnabled = false

        val flowLayout = FlowLayout(FlowLayout.RIGHT)
        flowLayout.hgap = 8
        val iconsPanel = JPanel(flowLayout)
        iconsPanel.add(
            IconActionButton(
                object : AnAction("Send Message", "Send message", VoqalIcons.send) {
                    override fun actionPerformed(e: AnActionEvent) {
                        handleSubmit()
                    }
                })
        )

        iconsPanel.add(stopButton)
        add(iconsPanel, BorderLayout.EAST)
    }

    private fun updateFont() {
        if (Registry.`is`("ide.find.use.editor.font", false)) {
            textArea.font = EditorUtil.getEditorFont()
        } else {
            textArea.font = UIManager.getFont("TextField.font")
        }
    }

    companion object {
        private val BACKGROUND_COLOR = JBColor.namedColor(
            "Editor.SearchField.background", UIUtil.getTextFieldBackground()
        )
    }
}
