package dev.voqal.ide.ui

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.ui.JBUI
import dev.voqal.ide.ui.toolwindow.chat.conversation.SmartScroller
import dev.voqal.services.ProjectScopedService
import org.intellij.lang.annotations.Language
import java.net.URISyntaxException
import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

object VoqalUI {

    fun createLogsViewer(project: Project): Editor {
        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createViewer(editorFactory.createDocument(""))
        Disposer.register(project.service<ProjectScopedService>()) {
            if (!editor.isDisposed) {
                editorFactory.releaseEditor(editor)
            }
        }
        editor.component.border = BorderFactory.createLineBorder(JBUI.CurrentTheme.ToolWindow.background())

        val settings = editor.settings
        settings.isLineNumbersShown = false
        settings.isLineMarkerAreaShown = false
        settings.isIndentGuidesShown = false
        settings.additionalColumnsCount = 0
        settings.additionalLinesCount = 0
        settings.setGutterIconsShown(false)
        settings.isIndentGuidesShown = false
        (editor.gutter as EditorGutterComponentEx).isPaintBackground = false
        return editor
    }

    @JvmStatic
    fun createPreviewComponent(
        project: Project,
        @Language("Markdown")
        content: String,
        editable: Boolean,
        parentDisposable: Disposable
    ): Editor {
        val cleanedContent = content.replace("\r\n", "\n")
        val file = LightVirtualFile("content.md", cleanedContent)
        val editorFactory = EditorFactory.getInstance()
        val document = try {
            editorFactory.createDocument(cleanedContent)
        } catch (e: Error) {
            editorFactory.createDocument("Failed to create document: ${e.message}")
        }
        val editor = editorFactory.createEditor(document, project, file, !editable)
        Disposer.register(parentDisposable) {
            if (!editor.isDisposed) {
                editorFactory.releaseEditor(editor)
            }
        }

        //make font 30% smaller
        editor.colorsScheme.editorFontSize = (editor.colorsScheme.editorFontSize * 0.7).toInt()

        return editor
    }

    @JvmOverloads
    fun createTextPane(
        text: String?,
        opaque: Boolean = true,
        listener: HyperlinkListener? = HyperlinkListener { obj: HyperlinkEvent -> handleHyperlinkClicked(obj) }
    ): JTextPane {
        val textPane = JTextPane()
        textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true)
        textPane.addHyperlinkListener(listener)
        textPane.contentType = "text/html"
        textPane.isEditable = false
        textPane.text = text
        textPane.isOpaque = opaque
        return textPane
    }

    fun createScrollPaneWithSmartScroller(scrollablePanel: ScrollablePanel): JScrollPane {
        val scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        SmartScroller(scrollPane)
        return scrollPane
    }

    fun handleHyperlinkClicked(event: HyperlinkEvent) {
        val url = event.url
        if (HyperlinkEvent.EventType.ACTIVATED == event.eventType && url != null) {
            try {
                BrowserUtil.browse(url.toURI())
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            }
        }
    }

    fun addShiftEnterInputMap(textArea: JTextArea, onSubmit: AbstractAction?) {
        textArea.inputMap.put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break")
        textArea.inputMap.put(KeyStroke.getKeyStroke("ENTER"), "text-submit")
        textArea.actionMap.put("text-submit", onSubmit)
    }
}
