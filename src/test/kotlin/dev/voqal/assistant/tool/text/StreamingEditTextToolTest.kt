package dev.voqal.assistant.tool.text

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class StreamingEditTextToolTest : JBTest() {

    fun `test streaming rename removing text`() {
        val responseCode = File("src/test/resources/edit-stream/isTest-to-test.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            EditTextTool().getFullTextAfterStreamEdits(
                responseCode,
                originalText,
                testEditor,
                project,
                previousStreamIndicator,
                streamIndicators
            )
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            fullTextWithEdits,
            originalText
                .replace("Boolean isTest", "Boolean test")
                .replace(
                    """
                    |    if (isTest) {
                    |      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(isTest));
                    |    } else {
                """.trimMargin(),
                    """
                    |    if (test) {
                    |      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(test));
                    |    } else {
                """.trimMargin(),
                )
        )
    }

    fun `test streaming rename adding text`() {
        val responseCode = File("src/test/resources/edit-stream/isTest-to-isTestIo.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            EditTextTool().getFullTextAfterStreamEdits(
                responseCode,
                originalText,
                testEditor,
                project,
                previousStreamIndicator,
                streamIndicators
            )
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            fullTextWithEdits,
            originalText
                .replace("Boolean isTest", "Boolean isTestIo")
                .replace(
                    """
                    |    if (isTest) {
                    |      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(isTest));
                    |    } else {
                """.trimMargin(),
                    """
                    |    if (isTestIo) {
                    |      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(isTestIo));
                    |    } else {
                """.trimMargin(),
                )
        )
    }

    fun `test streaming rename causing last line modification`() {
        val responseCode = File("src/test/resources/edit-stream/todoItemService-to-theService.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            EditTextTool().getFullTextAfterStreamEdits(
                responseCode,
                originalText,
                testEditor,
                project,
                previousStreamIndicator,
                streamIndicators
            )
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertNull(fullTextWithEdits)
    }

    fun `test streaming edit visible range`() {
        val responseCode = File("src/test/resources/edit-stream/add-import-scanner.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TextAdventureGame.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        var fullTextWithEdits: String? = null
        val testDocument = LightVirtualFile("TextAdventureGame.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testRange = ProperTextRange(14, 91)
        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)
            project.service<VoqalMemoryService>().putUserData("visibleRange", testRange)
            val rangeHighlighter = mock<RangeHighlighter> {
                on { isValid } doReturn true
                on { startOffset } doReturn testRange.startOffset
                on { endOffset } doReturn testRange.endOffset
            }
            project.service<VoqalMemoryService>().putUserData("visibleRangeHighlighter", rangeHighlighter)

            fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                EditTextTool().getFullTextAfterStreamEdits(
                    responseCode,
                    originalText,
                    testEditor,
                    project,
                    previousStreamIndicator,
                    streamIndicators
                )
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(fullTextWithEdits, responseCode)
    }
}
