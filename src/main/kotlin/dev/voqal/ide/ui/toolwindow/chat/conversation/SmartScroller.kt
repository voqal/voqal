package dev.voqal.ide.ui.toolwindow.chat.conversation

import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.text.DefaultCaret
import javax.swing.text.JTextComponent

class SmartScroller @JvmOverloads constructor(
    scrollPane: JScrollPane,
    scrollDirection: Int = VERTICAL,
    viewportPosition: Int = END
) : AdjustmentListener {

    private val viewportPosition: Int
    private var adjustScrollBar = true
    private var previousValue = -1
    private var previousMaximum = -1

    init {
        require(!(scrollDirection != HORIZONTAL && scrollDirection != VERTICAL)) { "invalid scroll direction specified" }
        require(!(viewportPosition != START && viewportPosition != END)) { "invalid viewport position specified" }

        this.viewportPosition = viewportPosition
        val scrollBar = if (scrollDirection == HORIZONTAL) {
            scrollPane.horizontalScrollBar
        } else {
            scrollPane.verticalScrollBar
        }

        scrollBar.addAdjustmentListener(this)

        if (scrollPane.viewport.view is JTextComponent) {
            val textComponent = scrollPane.viewport.view as JTextComponent
            val caret = textComponent.getCaret() as DefaultCaret
            caret.updatePolicy = DefaultCaret.NEVER_UPDATE
        }
    }

    override fun adjustmentValueChanged(e: AdjustmentEvent) {
        SwingUtilities.invokeLater { checkScrollBar(e) }
    }

    private fun checkScrollBar(e: AdjustmentEvent) {
        val scrollBar = e.source as JScrollBar
        val listModel = scrollBar.model
        var value = listModel.value
        val extent = listModel.extent
        val maximum = listModel.maximum

        val valueChanged = previousValue != value
        val maximumChanged = previousMaximum != maximum

        if (valueChanged && !maximumChanged) {
            adjustScrollBar = if (viewportPosition == START) {
                value != 0
            } else {
                value + extent >= maximum
            }
        }

        if (adjustScrollBar && viewportPosition == END) {
            scrollBar.removeAdjustmentListener(this)
            value = maximum - extent
            scrollBar.value = value
            scrollBar.addAdjustmentListener(this)
        }

        if (adjustScrollBar && viewportPosition == START) {
            scrollBar.removeAdjustmentListener(this)
            value = value + maximum - previousMaximum
            scrollBar.value = value
            scrollBar.addAdjustmentListener(this)
        }

        previousValue = value
        previousMaximum = maximum
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val START = 0
        private const val END = 1
    }
}
