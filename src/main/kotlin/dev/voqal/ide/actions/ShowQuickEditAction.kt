package dev.voqal.ide.actions

import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorEmbeddedComponentManager
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.view.FontLayoutService
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.system.CancelTool
import dev.voqal.assistant.tool.system.LooksGoodTool
import dev.voqal.assistant.tool.system.mode.ToggleEditModeTool
import dev.voqal.ide.ui.toolwindow.chat.conversation.UserPromptTextArea
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch
import org.joor.Reflect
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.border.EmptyBorder
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Displays [UserPromptTextArea] above the caret of the current [Editor] allowing for directive
 * input outside the Voqal Chat tool window.
 */
class ShowQuickEditAction : AnAction() {

    companion object {

        fun setEditRangeHighlighter(project: Project, editor: Editor, editRange: TextRange): RangeHighlighter {
            val textAttributes = TextAttributes().apply {
                val borderColor = JBUI.CurrentTheme.ActionButton.focusedBorder()
                Reflect.on(this).call("withAdditionalEffect", EffectType.ROUNDED_BOX, borderColor)
            }
            val quickEditRangeHighlighter = editor.markupModel.addRangeHighlighter(
                editRange.startOffset, editRange.endOffset,
                QUICK_EDIT_LAYER, textAttributes, HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", quickEditRangeHighlighter)
            project.getVoqalLogger(this::class).debug("Highlighted quick edit range: $editRange")
            return quickEditRangeHighlighter
        }

        internal const val QUICK_EDIT_LAYER = 6101
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.dataContext.getData("editor") as EditorImpl? ?: return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return

        project.scope.launch {
            triggerQuickEdit(project, editor, psiFile)
        }
    }

    private suspend fun triggerQuickEdit(project: Project, editor: EditorImpl, psiFile: PsiFile) {
        val log = project.getVoqalLogger(this::class)
        val status = project.service<VoqalStatusService>().getStatus()
        if (status == VoqalStatus.DISABLED) {
            log.warnChat("Ignoring quick edit action. Plugin is disabled")
            return
        }

        //only one quick edit inlay open at a time
        val memoryService = project.service<VoqalMemoryService>()
        val toolService = project.service<VoqalToolService>()
        if (status == VoqalStatus.EDITING) {
            val inlay = memoryService.getUserData("voqal.edit.inlay") as Inlay<*>?
            if (inlay != null) {
                toolService.blindExecute(LooksGoodTool())
            }
        }

        val editorWidthWatcher = EditorTextWidthWatcher(project, editor)
        val wrapperPanel = JPanel()
        wrapperPanel.border = EmptyBorder(2, 0, 2, 0)
        wrapperPanel.layout = BorderLayout()

        var earlyExit = false
        val userPromptTextArea = UserPromptTextArea { message ->
            project.scope.launch {
                val configService = project.service<VoqalConfigService>()
                if (!configService.getConfig().pluginSettings.enabled) {
                    log.warnChat("Plugin is disabled")
                    return@launch
                }

                if (message == "") {
                    earlyExit = true
                    toolService.blindExecute(LooksGoodTool())
                } else {
                    log.debug("Sending message: $message")
                    project.service<VoqalDirectiveService>().handleTranscription(
                        SpokenTranscript(message, null),
                        textOnly = true,
                        chatMessage = true
                    )
                }
            }
        }
        userPromptTextArea.allowEmptyText = true
        wrapperPanel.add(userPromptTextArea)
        //        editor.scrollingModel.addVisibleAreaListener(controlBar)

        val wrappedComponent = ComponentWrapper(project, editorWidthWatcher, wrapperPanel)

        editor.scrollPane.viewport.addComponentListener(editorWidthWatcher)
        //        Disposer.register(this) {
        //            editor.scrollPane.viewport.removeComponentListener(editorWidthWatcher)
        //        }
        //
        //        EditorUtil.disposeWithEditor(editor, this)

        val caretPosition = ReadAction.compute(ThrowableComputable { editor.caretModel.offset })
        val editRange = getQuickEditRange(project, editor, psiFile, caretPosition)
        if (editRange == null) {
            log.warn("Unable to find opening block to quick edit")
            return
        }

        //make sure we're in edit mode
        if (status != VoqalStatus.EDITING) {
            toolService.blindExecute(ToggleEditModeTool(), JsonObject().put("chatMessage", true))
        }

        //show quick edit inlay and edit range
        val quickEditRangeHighlighter = setEditRangeHighlighter(project, editor, editRange)
        project.invokeLater {
            EditorEmbeddedComponentManager.getInstance().addComponent(
                editor, wrappedComponent,
                EditorEmbeddedComponentManager.Properties(
                    EditorEmbeddedComponentManager.ResizePolicy.any(),
                    null,
                    true,
                    true,
                    0,
                    caretPosition
                )
            )?.also {
                memoryService.putUserData("voqal.edit.inlay", it)
                Disposer.register(it) {
                    editor.markupModel.removeHighlighter(quickEditRangeHighlighter)
                }
            }

            userPromptTextArea.textArea.addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent) {
                    if (e.keyChar.code == KeyEvent.VK_ESCAPE) {
                        earlyExit = true
                        project.scope.launch {
                            toolService.blindExecute(CancelTool())
                        }
                    }
                }
            })

//            userPromptTextArea.textArea.addFocusListener(object : FocusAdapter() {
//                override fun focusLost(e: FocusEvent?) {
//                    if (earlyExit) return
//                    project.scope.launch {
//                        toolService.blindExecute(LooksGoodTool())
//                    }
//                }
//            })

            userPromptTextArea.focus()
        }
    }

    private fun getQuickEditRange(
        project: Project,
        editor: EditorImpl,
        psiFile: PsiFile,
        caretPosition: Int
    ): ProperTextRange? {
        val caretLineRange = getFullLineRange(editor, caretPosition, caretPosition)
        var editRange = project.service<VoqalContextService>().getOpeningBlockAt(psiFile, caretLineRange)
            ?.textRange?.let { ProperTextRange(it.startOffset, it.endOffset) } ?: return null

        //prefer lines in range to exact range
        val documentText = editor.document.text
        val fullLineRange = getFullLineRange(editor, editRange.startOffset, editRange.endOffset)
        if (editRange.substring(documentText) == fullLineRange.substring(documentText).trim()) {
            editRange = fullLineRange
        }
        return editRange
    }

    private fun getFullLineRange(editor: Editor, startOffset: Int, endOffset: Int): ProperTextRange {
        return ProperTextRange(
            editor.document.getLineStartOffset(editor.document.getLineNumber(startOffset)),
            editor.document.getLineEndOffset(editor.document.getLineNumber(endOffset))
        )
    }

    private inner class EditorTextWidthWatcher(
        project: Project,
        private val editor: EditorImpl
    ) : ComponentAdapter() {

        var editorTextWidth: Int = 0

        private val maximumEditorTextWidth: Int
        private val verticalScrollbarFlipped: Boolean

        init {
            val metrics = editor.getFontMetrics(Font.PLAIN)
            val spaceWidth = FontLayoutService.getInstance().charWidth2D(metrics, ' '.code)
            @Suppress("MagicNumber") // -4 to create some space
            maximumEditorTextWidth = ceil(spaceWidth * (editor.settings.getRightMargin(editor.project)) - 4).toInt()

            val scrollbarFlip = editor.scrollPane.getClientProperty(JBScrollPane.Flip::class.java)
            verticalScrollbarFlipped =
                scrollbarFlip == JBScrollPane.Flip.HORIZONTAL || scrollbarFlip == JBScrollPane.Flip.BOTH

            editorTextWidth = calcWidth()

            project.messageBusConnection.subscribe(UISettingsListener.TOPIC, UISettingsListener {
                updateWidthForAllInlays()
            })
        }

        override fun componentResized(e: ComponentEvent) = updateWidthForAllInlays()
        override fun componentHidden(e: ComponentEvent) = updateWidthForAllInlays()
        override fun componentShown(e: ComponentEvent) = updateWidthForAllInlays()

        private fun updateWidthForAllInlays() {
            val newWidth = calcWidth()
            if (editorTextWidth == newWidth) return
            editorTextWidth = newWidth
        }

        //todo: make configurable and dynamic like ComponentSizeEvaluator.getDynamicSize
        private fun calcWidth(): Int {
            val visibleEditorTextWidth =
                editor.scrollPane.viewport.width - getVerticalScrollbarWidth() - getGutterTextGap()
            return min(max(visibleEditorTextWidth, 0), maximumEditorTextWidth)
        }

        private fun getVerticalScrollbarWidth(): Int {
            val width = editor.scrollPane.verticalScrollBar.width
            return if (!verticalScrollbarFlipped) width * 2 else width
        }

        private fun getGutterTextGap(): Int {
            return if (verticalScrollbarFlipped) {
                val gutter = (editor as EditorEx).gutterComponentEx
                gutter.width - gutter.whitespaceSeparatorOffset
            } else 0
        }
    }

    private inner class ComponentWrapper(
        project: Project,
        private val editorWidthWatcher: EditorTextWidthWatcher,
        private val component: JComponent
    ) : JBScrollPane(component) {

        private var scale = 1.0f

        init {
            isOpaque = false
            viewport.isOpaque = false

            border = JBUI.Borders.empty()
            viewportBorder = JBUI.Borders.empty()

            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBar.preferredSize = Dimension(0, 0)
            setViewportView(component)

            component.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    dispatchEvent(ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED))
                }
            })

            project.messageBusConnection.subscribe(UISettingsListener.TOPIC, UISettingsListener {
                scale = it.ideScale
                dispatchEvent(ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED))
            })
        }

        override fun getPreferredSize(): Dimension {
            return Dimension(
                (editorWidthWatcher.editorTextWidth * scale).toInt(),
                (component.preferredSize.height * scale).toInt()
            )
        }
    }
}
