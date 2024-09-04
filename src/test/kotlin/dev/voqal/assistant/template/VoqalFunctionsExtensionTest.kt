package dev.voqal.assistant.template

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.util.ProperTextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.descendants
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.template.ChunkTextExtension.ChunkTextFunction
import dev.voqal.config.settings.PromptSettings
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class VoqalFunctionsExtensionTest : JBTest() {

    fun `test inside function edit range1`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/kt/InsideFunction1.kt")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 10, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertEquals(8, markdown.lines().filter { it.trim().isNotBlank() }.count())
        assertFalse(markdown.contains("{"))
        assertFalse(markdown.contains("}"))

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        val blockText = psiFile.descendants().filter { it.toString().contains("BLOCK") }.first().text
            .substringAfter("{\n").substringBefore("}")
        assertTrue(markdown.contains(blockText))
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test inside function edit range2`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/kt/InsideFunction2.kt")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 10, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertEquals(8, markdown.lines().filter { it.trim().isNotBlank() }.count())
        assertFalse(markdown.contains("{"))
        assertFalse(markdown.contains("}"))

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        val blockText = psiFile.descendants().filter { it.toString().contains("BLOCK") }.last().text
            .substringAfter("{\n").substringBefore("}")
        assertTrue(markdown.contains(blockText))
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test between functions edit range`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/php/BetweenFunctions.php")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 10, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertEquals(7, markdown.lines().filter { it.trim().isNotBlank() }.count())

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        val funcText = psiFile.descendants().filter { it.toString().contains("MethodImpl:") }.first().text
        assertTrue(markdown.contains(funcText))

        val commonIndent = funcText.lines().first().takeWhile { it == ' ' }.length
        assertTrue(funcText.lines().all { it.startsWith(" ".repeat(commonIndent)) })
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test lots of mini functions edit range`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/java/MiniFunctions.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 10, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertEquals(9, markdown.lines().filter { it.trim().isNotBlank() }.count())

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        psiFile.descendants().filter {
            it.toString().contains("PsiMethod:")
        }.toList().dropLast(1).forEach {
            val funcText = it.text
            assertTrue(markdown.contains(funcText))
        }

        //todo: shouldn't end with new line
//        val commonIndent = markdown.lines().first().takeWhile { it == ' ' }.length
//        assertTrue(markdown.lines().all { it.startsWith(" ".repeat(commonIndent)) })
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test lots of fields edit range`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/java/ManyFields.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 20, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        val findField = "    public static final int"
        val fieldIndex = psiFile.text.indexOf(findField)
        assertTrue(markdown.contains(psiFile.text.substring(fieldIndex).substringBeforeLast("\n}")))

//        //todo: shouldn't end with new line
//        val commonIndent = markdown.lines().first().takeWhile { it == ' ' }.length
//        assertTrue(markdown.lines().all { it.startsWith(" ".repeat(commonIndent)) })
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test lots of fields edit range2`() {
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = false
        val codeFile = File("src/test/resources/java/ManyFields.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 23, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertTrue(markdown.lines().filter { it.trim().isNotBlank() }.count() <= 23)

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(testDocument)!!
        val findTest = "    protected"
        val testIndex = psiFile.text.indexOf(findTest)
        assertTrue(markdown.contains(psiFile.text.substring(testIndex).substringBeforeLast("\n}")))

//        //todo: shouldn't end with new line
//        val commonIndent = markdown.lines().first().takeWhile { it == ' ' }.length
//        assertTrue(markdown.lines().all { it.startsWith(" ".repeat(commonIndent)) })
        ChunkTextFunction.VISIBLE_RANGE_FALLBACK = true
    }

    fun `test plsql parser1`() {
        val codeFile = File("src/test/resources/java/large/PLSQLParser.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(141750, 142684)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 400, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertTrue(markdown.contains("class Open_statementContext"))
        assertTrue(markdown.lines().filter { it.trim().isNotBlank() }.count() > 30)
        assertTrue(markdown.lines().filter { it.trim().isNotBlank() }.count() < 400)
    }

    fun `test plsql parser2`() {
        val codeFile = File("src/test/resources/java/large/PLSQLParser.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, 1000)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 400, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertTrue(markdown.contains("// Generated"))
        assertTrue(markdown.contains("NULL=38,"))
    }

    fun `test plsql parser3`() {
        val codeFile = File("src/test/resources/java/large/PLSQLParser.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(549, 2180)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 400, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertFalse(markdown.contains("public class PLSQLParser extends Parser"))
    }

    fun `test empty class edit range`() {
        val codeFile = File("src/test/resources/java/EmptyClass.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(0, codeText.length)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 200, \"LINES\").codeWithCaret }}"
                ),
                languageModelSettings = mock {}
            ),
            ide = IdeContext(project, editorMock),
            developer = DeveloperContext(
                transcription = "",
                viewingCode = ViewingCode(codeText),
                viewingFile = virtualFile
            )
        )
        val markdown = directive.toMarkdown()
        assertEquals(3, markdown.lines().filter { it.trim().isNotBlank() }.count())
    }
}
