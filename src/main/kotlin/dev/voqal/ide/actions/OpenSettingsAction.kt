package dev.voqal.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import dev.voqal.config.configurable.VoqalConfigurable
import org.jetbrains.annotations.Nls
import java.util.function.Supplier

class OpenSettingsAction(text: @Nls String) : DumbAwareAction(Supplier { text }, AllIcons.General.Settings) {
    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, VoqalConfigurable::class.java)
    }
}
