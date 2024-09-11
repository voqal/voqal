package dev.voqal.assistant.tool.text

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DiffEditTest : JBTest() {

    fun `test replace single line`() {
        val codeText = """
            import java.util.Scanner;

            public class Main {
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
            
                    System.out.println("Hello, World!");
            
                    scanner.close();
                }
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("Main.java", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            -7|        System.out.println("Hello, World!");
            +7|        System.out.println("Goodbye, World!");
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        assertEquals(codeText.replace("Hello, World!", "Goodbye, World!"), document.text)
    }

    fun `test replace multiple lines`() {
        val codeText = """
            public class AddMethod {
                public int add(int x, int y) {
                    return x + y;
                }
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("AddMethod.java", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            -2|    public int add(int x, int y) {
            +2|    public int add(int a, int b) {
            -3|        return x + y;
            +3|        return a + b;
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        assertEquals(codeText.replace("x", "a").replace("y", "b"), document.text)
    }

    fun `test remove lines between non removed lines`() {
        val codeText = """
            public class RemoveMethod {
                public int add(int x, int y) {
                    return x + y;
                }
            
                public int subtract(int a, int b) {
                    return a - b;
                }
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("RemoveMethod.java", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            1|class RemoveMethod {
            2|    fun add(x: Int, y: Int): Int {
            3|        return x + y
            4|    }
            5|
            -6|    fun subtract(a: Int, b: Int): Int {
            -7|        return a - b
            -8|    }
            9|}
            
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        val finalText = """
            public class RemoveMethod {
                public int add(int x, int y) {
                    return x + y;
                }
            
            }
        """.trimIndent()
        assertEquals(finalText, document.text)
    }

    fun `test unified diff format`() {
        val codeText = """
            package main
            
            import "fmt"
            
            type RemoveMethod struct{}
            
            func (self RemoveMethod) add(x int, y int) int {
                return x + y
            }
            
            func (self RemoveMethod) subtract(a int, b int) int {
                return a - b
            }
        """.trimIndent()
        val virtualFile = myFixture.createFile("RemoveMethod.go", codeText)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val responseCode = """
            --- a/RemoveMethod.go
            +++ b/RemoveMethod.go
            @@ -6,11 +6,13 @@
             5|type RemoveMethod struct{}
             6|
             7|func (self RemoveMethod) add(x int, y int) int {
            -8|    return x + y
            +8|    fmt.Println("Adding", x, "and", y)
            +9|    return x + y
             9|}
             10|
             11|func (self RemoveMethod) subtract(a int, b int) int {
            -12|    return a - b
            +12|    fmt.Println("Subtracting", b, "from", a)
            +13|    return a - b
             13|}
             14|
        """.trimIndent()

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)

            val testRange = ProperTextRange(0, codeText.length)
            val testHighlighter = editor.markupModel.addRangeHighlighter(
                testRange.startOffset, testRange.endOffset,
                HighlighterLayer.SELECTION, TextAttributes(), HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("editRangeHighlighter", testHighlighter)

            WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                runBlocking {
                    EditTextTool().doDocumentEdits(project, responseCode, editor)
                }
            })
            project.service<VoqalStatusService>().update(VoqalStatus.IDLE)
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(editor)

        val finalText = """
            package main
            
            import "fmt"
            
            type RemoveMethod struct{}
            
            func (self RemoveMethod) add(x int, y int) int {
                fmt.Println("Adding", x, "and", y)
                return x + y
            }
            
            func (self RemoveMethod) subtract(a int, b int) int {
                fmt.Println("Subtracting", b, "from", a)
                return a - b
            }
        """.trimIndent()
        assertEquals(finalText, document.text)
    }
}
