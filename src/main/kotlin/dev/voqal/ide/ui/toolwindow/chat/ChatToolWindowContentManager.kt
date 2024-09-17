package dev.voqal.ide.ui.toolwindow.chat

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import dev.voqal.assistant.VoqalResponse
import dev.voqal.services.invokeLater

@Service(Service.Level.PROJECT)
class ChatToolWindowContentManager(private val project: Project) {

    fun toggleDebugChatVisible() {
        project.invokeLater {
            tryFindFirstChatTabContent().toggleDebugChatVisible()
        }
    }

    fun clearChat() {
        project.invokeLater {
            tryFindFirstChatTabContent().clearChat()
        }
    }

    fun addErrorMessage(response: String, speechId: String? = null) {
        project.invokeLater {
            tryFindFirstChatTabContent().addErrorMessage(response, speechId)
        }
    }

    fun addUserMessage(response: String, speechId: String? = null) {
        project.invokeLater {
            tryFindFirstChatTabContent().addUserMessage(response, speechId)
        }
    }

    fun addResponse(response: String, voqalResponse: VoqalResponse?) {
        project.invokeLater {
            tryFindFirstChatTabContent().addResponse(response, voqalResponse)
        }
    }

    fun updateDirectiveInput(partialTranscript: String) {
        project.invokeLater {
            tryFindFirstChatTabContent().updateDirectiveInput(partialTranscript)
        }
    }

    fun getDirectiveInput(): String {
        return tryFindFirstChatTabContent().getDirectiveInput()
    }

    private fun tryFindFirstChatTabContent(): ChatToolWindowTabPanel {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Voqal")!!
        return (toolWindow.contentManager.contents.first().component as ChatToolWindowPanel).tabPanel
    }
}
