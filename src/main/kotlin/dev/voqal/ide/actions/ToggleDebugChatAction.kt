package dev.voqal.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import dev.voqal.ide.ui.toolwindow.chat.ChatToolWindowContentManager
import org.jetbrains.annotations.Nls
import java.util.function.Supplier

class ToggleDebugChatAction(text: @Nls String) : DumbAwareAction(Supplier { text }, AllIcons.General.InspectionsEye) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        project.service<ChatToolWindowContentManager>().toggleDebugChatVisible()
    }
}
