package dev.voqal.assistant.template

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.util.ProperTextRange
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

class ChunkTextExtensionTest : JBTest() {

    fun `test smart chunk`() {
        val codeFile = File("src/test/resources/edit-stream/TodoItemController.java")
        val codeText = codeFile.readText().replace("\r\n", "\n")
        val virtualFile = myFixture.createFile(codeFile.name, codeText)
        val testDocument = virtualFile.getDocument()
        val markupModelMock = mock<MarkupModel> {
            on { addRangeHighlighter(anyInt(), anyInt(), anyInt(), any(), any()) } doReturn mock()
        }
        val editorMock = mock<Editor> {
            on { calculateVisibleRange() } doReturn ProperTextRange(833, 1569)
            on { document } doReturn testDocument
            on { markupModel } doReturn markupModelMock
        }

        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                promptSettings = PromptSettings(
                    provider = PromptSettings.PProvider.CUSTOM_TEXT,
                    promptText = "{{ chunkText(developer.viewingCode, 18, \"LINES\").code }}"
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
        assertEquals(18, markdown.trim().lines().count())
        assertTrue(markdown.startsWith("  public void createRoutes(Service server) {"))
        assertTrue(markdown.endsWith("  }\n"))

        val directive2 = directive.copy(
            assistant = directive.assistant.copy(
                promptSettings = directive.assistant.promptSettings?.copy(
                    promptText = "{{ chunkText(developer.viewingCode, 18, \"LINES\").codeWithLineNumbers }}"
                )
            )
        )
        val markdown2 = directive2.toMarkdown()
        assertTrue(markdown2.lines().first().startsWith("23|"))
        assertTrue(markdown2.lines().last { it.isNotEmpty() }.startsWith("40|"))
    }
}
