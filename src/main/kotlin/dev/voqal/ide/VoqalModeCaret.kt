package dev.voqal.ide

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CaretVisualAttributes
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.project.Project
import dev.voqal.services.VoqalStatusService
import dev.voqal.status.VoqalStatus
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import javax.swing.Icon
import javax.swing.JComponent

/**
 * Provides visual caret icons for Voqal modes.
 */
class VoqalModeCaret(
    project: Project,
    private val editor: Editor
) : JComponent(), ComponentListener, CaretListener, Disposable {

    @Volatile
    private var status: VoqalStatus = VoqalStatus.DISABLED

    init {
        editor.contentComponent.add(this)
        isVisible = true
        bounds = editor.contentComponent.bounds
        editor.caretModel.addCaretListener(this)
        editor.component.addComponentListener(this)

        project.service<VoqalStatusService>().onStatusChange(this) { voqalStatus, _ ->
            status = voqalStatus
        }
    }

    override fun componentResized(e: ComponentEvent) {
        bounds = getEditorBounds()
        repaint()
    }

    override fun componentMoved(e: ComponentEvent) {
        bounds = getEditorBounds()
        repaint()
    }

    override fun componentShown(e: ComponentEvent) = Unit
    override fun componentHidden(e: ComponentEvent) = Unit

    override fun caretPositionChanged(e: CaretEvent) {
        bounds = getEditorBounds()
        repaint()
    }

    private fun isEditorFocused(): Boolean = editor.contentComponent.isFocusOwner
    private fun getEditorBounds(): Rectangle = editor.scrollingModel.visibleArea

//    private fun getCaretPosition(caret: Caret): Point {
//        val p: Point = editor.visualPositionToXY(caret.visualPosition)
//        p.translate(-location.x, -location.y)
//        return p
//    }
//
//    private fun getCaretHeight(caret: Caret): Int {
//        val p1 = editor.visualPositionToXY(caret.visualPosition)
//        val p2 = editor.visualPositionToXY(VisualPosition(caret.visualPosition.line + 1, caret.visualPosition.column))
//        return p2.y - p1.y
//    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (!isEditorFocused()) return

        editor.caretModel.allCarets.forEach { caret ->
            val caretIcon: Icon?
            val caretColor: Color?
            when (status) {
                VoqalStatus.EDITING -> {
                    caretIcon = VoqalIcons.logoEditing
                    caretColor = VoqalIcons.YELLOW
                }

                else -> {
                    if (caret.visualAttributes.color != null) {
                        caret.visualAttributes = CaretVisualAttributes(null, CaretVisualAttributes.DEFAULT.weight)
                    }
                    return
                }
            }

            if (caret.visualAttributes.color != caretColor) {
                caret.visualAttributes = CaretVisualAttributes(caretColor, CaretVisualAttributes.DEFAULT.weight)
            }
//            val caretHeight = getCaretHeight(caret)
//            val caretPosition = getCaretPosition(caret)
//            val middleYOffset = (caretHeight - caretIcon.iconHeight) / 2
//
//            if (status in setOf(VoqalStatus.EDITING)) {
//                caretIcon.paintIcon(this, g, caretPosition.x + 4, caretPosition.y + middleYOffset)
//            }
        }
    }

    override fun dispose() {
        editor.contentComponent.remove(this)
        editor.caretModel.removeCaretListener(this)
        editor.component.removeComponentListener(this)
    }
}
