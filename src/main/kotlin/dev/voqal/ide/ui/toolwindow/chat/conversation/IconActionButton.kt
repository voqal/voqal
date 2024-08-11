package dev.voqal.ide.ui.toolwindow.chat.conversation

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.impl.ActionButton

class IconActionButton(action: AnAction) : ActionButton(
    action,
    getPresentation(action),
    ActionPlaces.TOOLWINDOW_CONTENT,
    ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE
) {
    companion object {
        private fun getPresentation(action: AnAction): Presentation {
            val actionPresentation = action.templatePresentation
            val presentation = Presentation(actionPresentation.text)
            presentation.icon = actionPresentation.icon
            return presentation
        }
    }
}
