package dev.voqal.ide.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import dev.voqal.config.configurable.PromptLibraryConfigurable

class OpenPromptLibraryAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, PromptLibraryConfigurable::class.java)
    }
}
