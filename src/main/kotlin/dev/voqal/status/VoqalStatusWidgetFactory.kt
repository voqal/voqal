package dev.voqal.status

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory

class VoqalStatusWidgetFactory : StatusBarEditorBasedWidgetFactory() {

    override fun getId(): String = "dev.voqal.widget"
    override fun getDisplayName(): String = "Voqal"

    override fun createWidget(project: Project): StatusBarWidget {
        return VoqalStatusBarWidget(project)
    }

    override fun disposeWidget(widget: StatusBarWidget) = Unit
}
