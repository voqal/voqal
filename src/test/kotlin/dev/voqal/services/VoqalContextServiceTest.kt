package dev.voqal.services

import com.intellij.lang.Language
import com.intellij.openapi.components.service
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
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import java.io.File

class VoqalContextServiceTest : JBTest() {

//    fun `test code structure`() {
//        val lang = Language.findLanguageByID(System.getenv("VQL_LANG") ?: "JAVA")!!
//        val fileExt = getFileExtensionForLanguage(lang)
//        val addBreakpointsFile = File("src/test/resources/$fileExt/AddBreakpoints.$fileExt")
//        val addBreakpointsCode = addBreakpointsFile.readText()
//        myFixture.addFileToProject("src/main/java/org/${addBreakpointsFile.name}", addBreakpointsCode)
//        val removeBreakpointsFile = File("src/test/resources/$fileExt/RemoveBreakpoints.$fileExt")
//        val removeBreakpointsCode = removeBreakpointsFile.readText()
//        myFixture.addFileToProject("src/main/java/org/example/${removeBreakpointsFile.name}", removeBreakpointsCode)
//
//        val test = project.service<VoqalSearchService>().getProjectCodeStructure()
//        println(test)
//    }

    fun `test project structure cropping`() {
        val lang = Language.findLanguageByID(System.getenv("VQL_LANG") ?: "JAVA")!!
        val fileExt = getFileExtensionForLanguage(lang)
        val removeBreakpointsFile = File("src/test/resources/$fileExt/RemoveBreakpoints.$fileExt")
        val removeBreakpointsCode = removeBreakpointsFile.readText()
        val virtualFile = LightVirtualFile(removeBreakpointsFile.name, lang, removeBreakpointsCode)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)
        myFixture.addFileToProject("test1.txt", "")
        myFixture.addFileToProject("test2.txt", "")
        myFixture.addFileToProject("test3.txt", "")
        myFixture.addFileToProject("test4.txt", "")
        myFixture.addFileToProject("test5.txt", "")

        val previousLanguageModelSettings = project.service<VoqalConfigService>().getConfig().languageModelsSettings
        val contextService = VoqalContextService(project)
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = emptyList(),
                languageModelSettings = previousLanguageModelSettings.copy(
                    models = previousLanguageModelSettings.models.map {
                        it.copy(tokenLimit = 100)
                    }
                ).models.first(),
                promptSettings = PromptSettings(promptName = "Idle Mode")
            ),
            ide = IdeContext(
                project,
                editor,
                projectFileTree = project.service<VoqalSearchService>().getProjectStructureAsMarkdownTree()
            ),
            developer = DeveloperContext(
                transcription = "",
                viewingFile = null,
                viewingCode = null,
                selectedCode = null,
                textOnly = true
            )
        )

        val testContext = VertxTestContext()
        project.scope.launch {
            testContext.verify {
                val contextLength = directive.toMarkdown().length
                val croppedDirective = contextService.cropAsNecessary(directive)
                val croppedLength = croppedDirective.toMarkdown().length
                project.invokeLater {
                    EditorFactory.getInstance().releaseEditor(editor)
                }
                assertTrue(croppedLength < contextLength)
                testContext.completeNow()
            }
        }
        errorOnTimeout(testContext)

        //reset token limit
        project.service<VoqalConfigService>().updateConfig(previousLanguageModelSettings)
    }

    fun `test pretty prompt`() {
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = emptyList(),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first(),
                promptSettings = PromptSettings(promptName = "Idle Mode")
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(
                transcription = "",
                viewingFile = null,
                viewingCode = null,
                selectedCode = null,
                textOnly = true
            )
        )

        val testContext = VertxTestContext()
        project.scope.launch {
            testContext.verify {
                val markdown = directive.toMarkdown()
                assertEquals(0, Regex("\n\n\n").findAll(markdown).count())
                testContext.completeNow()
            }
        }
        errorOnTimeout(testContext)
    }

    fun `test pretty prompt ignores code block`() {
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = emptyList(),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first(),
                promptSettings = PromptSettings(promptName = "Idle Mode")
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(
                transcription = "",
                viewingFile = null,
                viewingCode = ViewingCode(
                    code = "1\n\n\n2",
                    codeWithLineNumbers = "1\n\n\n2" //force for testing
                ),
                selectedCode = null,
                textOnly = true
            )
        )

        val testContext = VertxTestContext()
        project.scope.launch {
            testContext.verify {
                val markdown = directive.toMarkdown()
                assertEquals(1, Regex("\n\n\n").findAll(markdown).count())
                testContext.completeNow()
            }
        }
        errorOnTimeout(testContext)
    }

    fun `test visible code cropping`() {
        val lang = Language.findLanguageByID(System.getenv("VQL_LANG") ?: "JAVA")!!
        val fileExt = getFileExtensionForLanguage(lang)
        val largeCodeFile = File("src/test/resources/$fileExt/large/PLSQLParser.$fileExt")
        val largeCode = largeCodeFile.readText()
        val virtualFile = LightVirtualFile(largeCodeFile.name, lang, largeCode)
        val document = virtualFile.getDocument()
        val editor = EditorFactory.getInstance().createEditor(document)

        val previousLanguageModelSettings = project.service<VoqalConfigService>().getConfig().languageModelsSettings
        val contextService = VoqalContextService(project)
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = emptyList(),
                languageModelSettings = previousLanguageModelSettings.copy(
                    models = previousLanguageModelSettings.models.map {
                        it.copy(tokenLimit = 100)
                    }
                ).models.first(),
                promptSettings = PromptSettings(promptName = "Edit Mode")
            ),
            ide = IdeContext(project, editor),
            developer = DeveloperContext(
                transcription = "",
                viewingFile = null,
                viewingCode = ViewingCode(
                    code = largeCode,
                    language = lang.id,
                    filename = largeCodeFile.path
                ),
                selectedCode = null,
                textOnly = true
            )
        )

        val testContext = VertxTestContext()
        project.scope.launch {
            testContext.verify {
                val contextLength = directive.toMarkdown().length
                val croppedDirective = contextService.cropAsNecessary(directive)
                val croppedLength = croppedDirective.toMarkdown().length
                project.invokeLater {
                    EditorFactory.getInstance().releaseEditor(editor)
                }
                assertTrue(croppedLength < contextLength)
                testContext.completeNow()
            }
        }
        errorOnTimeout(testContext)

        //reset token limit
        project.service<VoqalConfigService>().updateConfig(previousLanguageModelSettings)
    }
}
