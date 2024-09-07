package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.ColorUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import dev.voqal.ide.VoqalIcons
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingConstants

open class ChatMessagePanel(
    val isUser: Boolean = false,
    val isDebug: Boolean = false,
    val isError: Boolean = false
) : JPanel(BorderLayout()) {
    private val header: Header
    private val body: Body

    init {
        header = Header()
        body = Body()
        add(header, BorderLayout.NORTH)
        add(body, BorderLayout.CENTER)
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

    val content: JComponent?
        get() = body.content

    inner class Header : JPanel(BorderLayout()) {
        private val iconsWrapper: JPanel

        init {
            border = JBUI.Borders.empty(12, 8, 4, 8)
            add(iconLabel, BorderLayout.LINE_START)

            iconsWrapper = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0))
            if (this@ChatMessagePanel.isError) {
                iconsWrapper.background = VoqalIcons.DARK_RED
                background = VoqalIcons.DARK_RED
            } else if (this@ChatMessagePanel.isDebug) {
                iconsWrapper.background = background.darker()
                background = background.darker()
            } else if (this@ChatMessagePanel.isUser) {
                iconsWrapper.background = ColorUtil.brighter(background, 2)
                background = ColorUtil.brighter(background, 2)
            } else {
                iconsWrapper.background = background
                background = background
            }
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

    inner class Body : JPanel(BorderLayout()) {
        var content: JComponent? = null
            private set

        init {
            border = JBUI.Borders.empty(4, 8, 8, 8)
            if (this@ChatMessagePanel.isError) {
                background = VoqalIcons.DARK_RED
            } else if (this@ChatMessagePanel.isDebug) {
                background = background.darker()
            } else if (this@ChatMessagePanel.isUser) {
                background = ColorUtil.brighter(background, 2)
            }
        }

        fun addContent(content: JComponent) {
            this.content = content
            add(content)
        }
    }
}
