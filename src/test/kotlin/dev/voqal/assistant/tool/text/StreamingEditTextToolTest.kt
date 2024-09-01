package dev.voqal.assistant.tool.text

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.JBTest
import kotlinx.coroutines.runBlocking
import java.io.File

class StreamingEditTextToolTest : JBTest() {

    fun `test streaming rename removing text`() {
        val responseCode = File("src\\test\\resources\\edit-stream\\isTest-to-test.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src\\test\\resources\\edit-stream\\TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = EditorFactory.getInstance().createDocument(originalText)
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            runBlocking {
                EditTextTool().getFullTextAfterStreamEdits(
                    responseCode,
                    originalText,
                    testEditor,
                    project,
                    previousStreamIndicator,
                    streamIndicators
                )
            }
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
        val responseCode = File("src\\test\\resources\\edit-stream\\isTest-to-isTestIo.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src\\test\\resources\\edit-stream\\TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = EditorFactory.getInstance().createDocument(originalText)
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            runBlocking {
                EditTextTool().getFullTextAfterStreamEdits(
                    responseCode,
                    originalText,
                    testEditor,
                    project,
                    previousStreamIndicator,
                    streamIndicators
                )
            }
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
        val responseCode = File("src\\test\\resources\\edit-stream\\todoItemService-to-theService.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src\\test\\resources\\edit-stream\\TodoItemController.java").readText()
            .replace("\r\n", "\n")
        val previousStreamIndicator = null
        val streamIndicators = mutableListOf<RangeHighlighter>()

        val testDocument = EditorFactory.getInstance().createDocument(originalText)
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val fullTextWithEdits = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
            runBlocking {
                EditTextTool().getFullTextAfterStreamEdits(
                    responseCode,
                    originalText,
                    testEditor,
                    project,
                    previousStreamIndicator,
                    streamIndicators
                )
            }
        })
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertNull(fullTextWithEdits)
    }
}
