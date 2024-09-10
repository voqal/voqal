package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import dev.voqal.ide.VoqalIcons
import dev.voqal.services.invokeLater
import dev.voqal.services.messageBusConnection
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingConstants

open class ChatMessagePanel(
    project: Project,
    val isUser: Boolean = false,
    val isDebug: Boolean = false,
    val isError: Boolean = false
) : JPanel(BorderLayout()) {

    val content: JComponent?
        get() = body.content

    private val header: Header
    private val body: Body

    init {
        header = Header()
        body = Body()
        add(header, BorderLayout.NORTH)
        add(body, BorderLayout.CENTER)

        updateUiColors()
        project.messageBusConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener {
            project.invokeLater {
                updateUiColors()
            }
        })
    }

    private fun updateUiColors() {
        val defaultBackground = EditorColorsManager.getInstance().globalScheme.defaultBackground

        if (isError) {
            background = VoqalIcons.DARK_RED
        } else if (isDebug) {
            background = defaultBackground.darker()
        } else if (!isUser) {
            background = ColorUtil.brighter(defaultBackground, 2)
        }
    }

    fun withViewPrompt(onPromptCopy: Runnable): ChatMessagePanel {
        header.addViewPromptAction(onPromptCopy)
        return this
    }

    fun withPlayDeveloperSpeech(onAction: Runnable): ChatMessagePanel {
        header.addPlayDeveloperSpeechAction(onAction)
        return this
    }

    fun addContent(content: JComponent): ChatMessagePanel {
        body.addContent(content)
        return this
    }

    private inner class Header : JPanel(BorderLayout()) {
        private val iconsWrapper: JPanel

        init {
            isOpaque = false
            border = JBUI.Borders.empty(12, 8, 4, 8)
            add(iconLabel, BorderLayout.LINE_START)

            iconsWrapper = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0))
            iconsWrapper.isOpaque = false
            add(iconsWrapper, BorderLayout.LINE_END)
        }

        fun addViewPromptAction(runnable: Runnable) {
            addIconActionButton(
                IconActionButton(
                    object : AnAction("View Prompt", "View prompt", AllIcons.General.InspectionsEye) {
                        override fun actionPerformed(e: AnActionEvent) {
                            runnable.run()
                        }
                    }
                )
            )
        }

        fun addPlayDeveloperSpeechAction(runnable: Runnable) {
            addIconActionButton(
                IconActionButton(
                    object : AnAction("Play Speech", "Play speech", VoqalIcons.volume) {
                        override fun actionPerformed(e: AnActionEvent) {
                            runnable.run()
                        }
                    }
                )
            )
        }

        private fun addIconActionButton(iconActionButton: IconActionButton) {
            if (iconsWrapper.components != null && iconsWrapper.components.isNotEmpty()) {
                iconsWrapper.add(Box.createHorizontalStrut(8))
            }
            iconsWrapper.add(iconActionButton)
        }

        private val iconLabel: JBLabel
            get() = JBLabel(
                if (this@ChatMessagePanel.isError) {
                    "Error"
                } else if (this@ChatMessagePanel.isDebug) {
                    "Debug"
                } else if (this@ChatMessagePanel.isUser) {
                    getDisplayName()
                } else "Voqal",
                if (this@ChatMessagePanel.isError) {
                    VoqalIcons.Error
                } else if (this@ChatMessagePanel.isDebug) {
                    VoqalIcons.Debug
                } else if (this@ChatMessagePanel.isUser) {
                    VoqalIcons.User
                } else VoqalIcons.Default,
                SwingConstants.LEADING
            )
                .setAllowAutoWrapping(true)
                .withFont(JBFont.label().asBold())

        fun getDisplayName(): String {
            val systemUserName = System.getProperty("user.name")
            if (systemUserName == null || systemUserName.isEmpty()) {
                return "User"
            }
            return systemUserName
        }
    }

    private inner class Body : JPanel(BorderLayout()) {
        var content: JComponent? = null
            private set

        init {
            isOpaque = false
            border = JBUI.Borders.empty(4, 8, 8, 8)
        }

        fun addContent(content: JComponent) {
            this.content = content
            add(content)
        }
    }
}
