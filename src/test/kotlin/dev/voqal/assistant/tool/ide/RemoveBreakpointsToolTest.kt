package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.ToolCall
import com.intellij.lang.Language
import com.intellij.openapi.editor.EditorFactory
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.tool.code.CreateClassTool.Companion.getFileExtensionForLanguage
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking
import java.io.File

class RemoveBreakpointsToolTest : JBTest() {

    fun `test remove all breakpoints`(): Unit = runBlocking {
        val log = project.getVoqalLogger(this::class)
        if (System.getenv("VQL_LANG") !in setOf(null, "JAVA")) {
            log.info("Ignoring java test in non-java mode")
            return@runBlocking
        }
        val lang = Language.findLanguageByID(System.getenv("VQL_LANG") ?: "JAVA")!!
        log.info("Testing language: $lang")

        val fileExt = getFileExtensionForLanguage(lang)
        val removeBreakpointsFile = File("src/test/resources/$fileExt/RemoveBreakpoints.$fileExt")
        val removeBreakpointsCode = removeBreakpointsFile.readText()
        val virtualFile = LightVirtualFile(removeBreakpointsFile.name, lang, removeBreakpointsCode)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)
        EditorFactory.getInstance().releaseEditor(editor)

        val transcription = "Remove all breakpoints"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = listOf(RemoveBreakpointsTool()),
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            ide = IdeContext(project, editor),
            developer = DeveloperContext(
                transcription = transcription,
                viewingCode = ViewingCode(removeBreakpointsCode),
                viewingFile = virtualFile,
                textOnly = true,
                activeBreakpoints = listOf(3, 4)
            )
        )
        val response = directive.assistant.memorySlice.addMessage(directive)

        assertEquals(response.toString(), 1, response.toolCalls.size)
        val toolCall = response.toolCalls[0] as ToolCall.Function
        val functionCall = toolCall.function
        assertEquals(RemoveBreakpointsTool.NAME, functionCall.name)

        val json = JsonObject(functionCall.arguments)
        val lineNumbers = json.getJsonArray("line_numbers").map { it.toString().toInt() }
        assertEquals(listOf(3, 4), lineNumbers)
    }
}
