package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.CurrentTheme
import com.intellij.util.ui.UIUtil
import dev.voqal.ide.VoqalIcons
import dev.voqal.ide.ui.VoqalUI.addShiftEnterInputMap
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.function.Consumer
import javax.swing.AbstractAction
import javax.swing.JPanel
import javax.swing.UIManager

class UserPromptTextArea(
    private val onSubmit: Consumer<String>,
) : JPanel(BorderLayout()) {

    val textArea = JBTextArea()
    private val textAreaRadius = 4
    private var stopButton: IconActionButton? = null
    private var submitEnabled = true
    var allowEmptyText = false

    init {
        textArea.isOpaque = false
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.emptyText.setText("").appendText(
            true,
            0,
            VoqalIcons.microphone,
            "Speak or type here",
            SimpleTextAttributes.REGULAR_ATTRIBUTES,
            object : ActionListener {
                override fun actionPerformed(e: ActionEvent?) {
                    println("here")
                }
            })

        textArea.putClientProperty(UIUtil.HIDE_EDITOR_FROM_DATA_CONTEXT_PROPERTY, true)

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
        g2.drawRoundRect(0, 0, width - 1, height - 1, textAreaRadius, textAreaRadius)
    }

    override fun getInsets(): Insets {
        return JBUI.insets(6, 12, 6, 6)
    }

    private fun handleSubmit() {
        if (submitEnabled && (textArea.text.isNotEmpty() || allowEmptyText)) {
            onSubmit.accept(text.trim { it <= ' ' })
            textArea.text = ""
        }
    }

    private fun init() {
        isOpaque = false
        add(textArea, BorderLayout.CENTER)

        stopButton = IconActionButton(
            object : AnAction("Cancel", "Cancel message", AllIcons.Actions.Suspend) {
                override fun actionPerformed(e: AnActionEvent) {
                }
            })
        stopButton!!.isEnabled = false

        val flowLayout = FlowLayout(FlowLayout.RIGHT, 6, 0)
        val iconsPanel = JPanel(flowLayout)
        iconsPanel.add(
            IconActionButton(
                object : AnAction("Send Directive", "Send directive", VoqalIcons.send) {
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
}
