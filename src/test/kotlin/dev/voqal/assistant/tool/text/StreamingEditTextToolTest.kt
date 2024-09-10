package dev.voqal.assistant.tool.text

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.ProperTextRange
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import java.io.File

class StreamingEditTextToolTest : JBTest() {

    fun `test streaming rename removing text`() {
        val responseCode = File("src/test/resources/edit-stream/isTest-to-test.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(5, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(4, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 453 && it.endOffset == 457 })
                assertTrue(editHighlighters[1].let { it.startOffset == 469 && it.endOffset == 473 })
                assertTrue(editHighlighters[2].let { it.startOffset == 549 && it.endOffset == 553 })
                assertTrue(editHighlighters[3].let { it.startOffset == 642 && it.endOffset == 646 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            originalText
                .replace("isTest", "test")
                .replace("TodoItemInMemoryRepository(test)", "TodoItemInMemoryRepository(isTest)")
        )
    }

    fun `test streaming rename adding text`() {
        val responseCode = File("src/test/resources/edit-stream/isTest-to-isTestIo.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(5, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(4, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 453 && it.endOffset == 461 })
                assertTrue(editHighlighters[1].let { it.startOffset == 473 && it.endOffset == 481 })
                assertTrue(editHighlighters[2].let { it.startOffset == 557 && it.endOffset == 565 })
                assertTrue(editHighlighters[3].let { it.startOffset == 654 && it.endOffset == 662 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            originalText
                .replace("isTest", "isTestIo")
                .replace("TodoItemInMemoryRepository(isTestIo)", "TodoItemInMemoryRepository(isTest)")
        )
    }

    fun `test streaming rename causing last line modification`() {
        val responseCode = File("src/test/resources/edit-stream/todoItemService-to-theService.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(1, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(0, editHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(testEditor.document.text, originalText) //no changes
    }

    fun `test streaming rename causing last line modification2`() {
        val responseCode = File("src/test/resources/edit-stream/todoItemService-to-theService-2.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(7, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(6, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 399 && it.endOffset == 409 })
                assertTrue(editHighlighters[1].let { it.startOffset == 482 && it.endOffset == 492 })
                assertTrue(editHighlighters[2].let { it.startOffset == 572 && it.endOffset == 582 })
                assertTrue(editHighlighters[3].let { it.startOffset == 1012 && it.endOffset == 1022 })
                assertTrue(editHighlighters[4].let { it.startOffset == 1197 && it.endOffset == 1207 })
                assertTrue(editHighlighters[5].let { it.startOffset == 1473 && it.endOffset == 1483 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            originalText
                .replace("todoItemService", "theService")
                .replace("//      theService", "//      todoItemService")
        )
    }

    fun `test streaming rename causing last line modification3`() {
        val responseCode = File("src/test/resources/edit-stream/todoItemService-to-theService-3.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(9, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(8, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 399 && it.endOffset == 409 })
                assertTrue(editHighlighters[1].let { it.startOffset == 482 && it.endOffset == 492 })
                assertTrue(editHighlighters[2].let { it.startOffset == 572 && it.endOffset == 582 })
                assertTrue(editHighlighters[3].let { it.startOffset == 651 && it.endOffset == 661 })
                assertTrue(editHighlighters[4].let { it.startOffset == 733 && it.endOffset == 743 })
                assertTrue(editHighlighters[5].let { it.startOffset == 1002 && it.endOffset == 1012 })
                assertTrue(editHighlighters[6].let { it.startOffset == 1187 && it.endOffset == 1197 })
                assertTrue(editHighlighters[7].let { it.startOffset == 1463 && it.endOffset == 1473 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            originalText.replace("todoItemService", "theService")
        )
    }

    fun `test streaming edit visible range`() {
        val responseCode = File("src/test/resources/edit-stream/add-import-scanner.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TextAdventureGame.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TextAdventureGame.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(14, 91)
            project.service<VoqalMemoryService>().putUserData("visibleRange", testRange)
            val testHighlighter = testEditor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("visibleRangeHighlighter", testHighlighter)

            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)

            testContext.verify {
                assertEquals(2, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(1, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 91 && it.endOffset == 117 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            originalText.replace("import java.util.List;", "import java.util.List;\nimport java.util.Scanner;")
        )
    }

    fun `test streaming edit groq bad spacing`() {
        val responseCode = File("src/test/resources/edit-stream/groq-bad-spacing.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(1, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(0, editHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(testEditor.document.text, originalText) //no changes
    }

    fun `test streaming edit groq bad spacing2`() {
        val responseCode = File("src/test/resources/edit-stream/groq-bad-spacing-2.txt").readText()
            .replace("\r\n", "\n")
        val originalText = File("src/test/resources/edit-stream/TodoItemController.java").readText()
            .replace("\r\n", "\n")

        val testDocument = LightVirtualFile("TodoItemController.java", originalText).getDocument()
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(1, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(0, editHighlighters.size)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(testEditor.document.text, originalText) //no changes
    }

    fun `test streaming edit ignore offsets outside current file`() {
        val responseCode = File("src/test/resources/edit-stream/rename-myMethod-to-test.txt").readText()
            .replace("\r\n", "\n")
        val fileOneText = File("src/test/resources/edit-stream/FileOne.java").readText()
            .replace("\r\n", "\n")
        val fileTwoText = File("src/test/resources/edit-stream/FileTwo.java").readText()
            .replace("\r\n", "\n")

        val fileOnePsi = myFixture.addFileToProject("FileOne.java", fileOneText)
        myFixture.addFileToProject("FileTwo.java", fileTwoText)

        val testDocument = fileOnePsi.fileDocument
        val testEditor = EditorFactory.getInstance().createEditor(testDocument, project)
        val testContext = VertxTestContext()
        project.scope.launch {
            val voqalHighlighters = EditTextTool().doDocumentEdits(project, responseCode, testEditor, true)
            testContext.verify {
                assertEquals(3, voqalHighlighters.size)

                val editHighlighters = voqalHighlighters.filter { it.layer == EditTextTool.ACTIVE_EDIT_LAYER }
                assertEquals(2, editHighlighters.size)
                assertTrue(editHighlighters[0].let { it.startOffset == 39 && it.endOffset == 43 })
                assertTrue(editHighlighters[1].let { it.startOffset == 91 && it.endOffset == 95 })
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(testEditor)

        assertEquals(
            testEditor.document.text,
            fileOneText.replace("myMethod", "test")
        )
    }
}
