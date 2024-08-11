package dev.voqal.ide.ui.toolwindow.chat

import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import dev.voqal.ide.ui.VoqalUI.createTextPane
import dev.voqal.ide.ui.toolwindow.chat.conversation.ChatMessagePanel
import dev.voqal.services.invokeLater
import java.awt.BorderLayout
import javax.swing.JPanel

class ChatToolWindowLandingPanel(project: Project) : ChatMessagePanel() {

    init {
        val firstMessage = RunOnceUtil.runOnceForApp("VoqalSetup") {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            val myToolWindow = toolWindowManager.getToolWindow("Voqal")
            if (myToolWindow != null) {
                project.invokeLater {
                    myToolWindow.show()
                }
            }
        }
        addContent(createContent(firstMessage))
    }

    private fun createContent(firstMessage: Boolean): JPanel {
        return JPanel(BorderLayout()).apply {
            add(createTextPane(getWelcomeMessage(firstMessage), false), BorderLayout.NORTH)
        }
    }

    private fun getWelcomeMessage(firstMessage: Boolean): String {
        val welcome = buildString {
            append("<html>")
            append("<p style=\"margin-top: 4px; margin-bottom: 4px;\">")
            append("Hello, my name is Voqal. I am your vocal programming assistant. ")
            append("To get started, you will need to set up my AI providers. ")
            append("You can configure my AI providers at: Settings >> Tools >> Voqal. ")
            append("Please note that I will be unable to respond until you have properly configured me.<br><br>")
            append("If you need help, please go to <a href=\"https://docs.voqal.dev\">docs.voqal.dev</a> for more information.")
            append("</p>")
            append("</html>")
        }
        if (firstMessage) return welcome

        val name = System.getProperty("user.name")
        if (name != null) {
            return """
                <html>
                <p style="margin-top: 4px; margin-bottom: 4px;">
                Hello, $name! How can I assist you today?
                </p>
                </html>
            """.trimIndent()
        }
        return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            How can I assist you today?
            </p>
            </html>
        """.trimIndent()
    }
}
