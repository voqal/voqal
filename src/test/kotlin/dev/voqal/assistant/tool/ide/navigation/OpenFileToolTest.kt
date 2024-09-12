package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.ToolCall
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.replaceService
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.VoqalSearchService
import dev.voqal.services.VoqalToolService
import dev.voqal.services.scope
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class OpenFileToolTest : JBTest() {

    fun `test open exact file`() {
        val toolService = project.service<VoqalToolService>()
        val transcription = "Open the user management file"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = toolService.getAvailableTools().values,
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(transcription = transcription)
        )
        val response = launchAndReturn { directive.assistant.memorySlice.addMessage(directive) }

        assertEquals(1, response.toolCalls.size)//, response.toString())
        val toolCall = response.toolCalls[0] as ToolCall.Function
        val functionCall = toolCall.function
        assertEquals(OpenFileTool.NAME, functionCall.name)

        val json = JsonObject(functionCall.arguments)
        assertTrue(
            json.getString("name").lowercase() in setOf(
                "usermanagement", "user management", "user_management",
                "user management class", "user_management_class",
                "user-management"
            )//, json.getString("name")
        )

        myFixture.addFileToProject("UserProfile.java", "")
        myFixture.addFileToProject("UserManagement.java", "")
        myFixture.addFileToProject("UserAccount.java", "")

        val testContext = VertxTestContext()
        val mockEditorManager = mock<FileEditorManagerEx> {
            on { getProject() } doReturn (project)
            on { openFile(any(), anyBoolean()) } doAnswer {
                testContext.verify {
                    assertEquals("/UserManagement.java", (it.arguments[0] as VirtualFile).path)
                }
                testContext.completeNow()
                arrayOf(mock<FileEditor>())
            }
        }
        project.replaceService(FileEditorManager::class.java, mockEditorManager, testRootDisposable)

        val file = launchAndReturn {
            project.service<VoqalSearchService>().findFile(json.getString("name"), false)
        }
        assertEquals("UserManagement.java", file?.name)
    }

    fun `test open exact class`() {
        val toolService = project.service<VoqalToolService>()
        val transcription = "Open the user management class"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = toolService.getAvailableTools().values,
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(transcription = transcription)
        )
        val response = launchAndReturn { directive.assistant.memorySlice.addMessage(directive) }

        assertEquals(1, response.toolCalls.size)//, response.toString())
        val toolCall = response.toolCalls[0] as ToolCall.Function
        val functionCall = toolCall.function
        assertEquals(OpenFileTool.NAME, functionCall.name)

        val json = JsonObject(functionCall.arguments)
        assertTrue(
            json.getString("name").lowercase() in setOf(
                "usermanagement", "user management", "user_management",
                "user management class", "user_management_class",
                "user-management"
            )//, json.getString("name")
        )

        val testContext = VertxTestContext()
        val mockEditorManager = mock<FileEditorManagerEx> {
            on { getProject() } doReturn (project)
            on { openFile(any(), anyBoolean()) } doAnswer {
                testContext.verify {
                    assertEquals("/UserManagement.java", (it.arguments[0] as VirtualFile).path)
                }
                testContext.completeNow()
                arrayOf(mock<FileEditor>())
            }
        }

        myFixture.addFileToProject("UserProfile.java", "")
        myFixture.addFileToProject("UserManagement.java", "")
        myFixture.addFileToProject("UserAccount.java", "")
        project.replaceService(FileEditorManager::class.java, mockEditorManager, testRootDisposable)

        val file = launchAndReturn {
            project.service<VoqalSearchService>().findFile(json.getString("name"), false)
        }
        assertEquals("UserManagement.java", file?.name)
    }

    fun `test close match`() {
        val toolService = project.service<VoqalToolService>()
        val transcription = "Open the user management file"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = toolService.getAvailableTools().values,
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(transcription = transcription)
        )
        val response = launchAndReturn { directive.assistant.memorySlice.addMessage(directive) }

        assertEquals(1, response.toolCalls.size)//, response.toString())
        val toolCall = response.toolCalls[0] as ToolCall.Function
        val functionCall = toolCall.function
        assertEquals(OpenFileTool.NAME, functionCall.name)

        val json = JsonObject(functionCall.arguments)
        assertTrue(
            json.getString("name").lowercase() in setOf(
                "usermanagement", "user management", "user_management",
                "user management class", "user_management_class",
                "user-management"
            )//, json.getString("name")
        )

        myFixture.addFileToProject("UserProfile.java", "")
        myFixture.addFileToProject("UserAddress.java", "")
        myFixture.addFileToProject("UserAccount.java", "")
        myFixture.addFileToProject("UserSubscription.java", "")
        myFixture.addFileToProject("UserManager.java", "")
        myFixture.addFileToProject("UserSettings.java", "")
        myFixture.addFileToProject("UserDashboard.java", "")

        val result = launchAndReturn {
            project.service<VoqalSearchService>().findFile(json.getString("name"), false)
        }
        assertEquals("UserManager.java", result?.name)
    }

    fun `test no files`() {
        val toolService = project.service<VoqalToolService>()
        val transcription = "Open the user management file"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = toolService.getAvailableTools().values,
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(transcription = transcription)
        )
        val response = launchAndReturn { directive.assistant.memorySlice.addMessage(directive) }

        assertEquals(1, response.toolCalls.size)//, response.toString())
        val toolCall = response.toolCalls[0] as ToolCall.Function
        val functionCall = toolCall.function
        assertEquals(OpenFileTool.NAME, functionCall.name)

        val json = JsonObject(functionCall.arguments)
        assertTrue(
            json.getString("name") in setOf(
                "user management", "user_management"
            )//, json.getString("name")
        )

        val result = launchAndReturn {
            project.service<VoqalSearchService>().findFile(json.getString("name"))
        }
        assertNull(result)
    }

    fun `test open file shortcut1`() {
        myFixture.addFileToProject("Main.java", "")

        val tool = OpenFileTool()
        val call1 = FunctionCall(
            nameOrNull = OpenFileTool.NAME,
            argumentsOrNull = JsonObject().put("directive", "open main.").toString()
        )
        assertTrue(tool.canShortcut(project, call1))

        val call2 = FunctionCall(
            nameOrNull = OpenFileTool.NAME,
            argumentsOrNull = JsonObject().put("directive", "open Main,").toString()
        )
        assertTrue(tool.canShortcut(project, call2))
    }

    fun `test open file camel case`() {
        val f1 = myFixture.addFileToProject("GoodbyeWorld.java", "")
        myFixture.addFileToProject("HelloWorld.java", "")

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalToolService>().blindExecute(
                OpenFileTool(), JsonObject().put("directive", "open goodbye world")
            )
            testContext.completeNow()
        }
        errorOnTimeout(testContext)

        assertEquals(1, FileEditorManager.getInstance(project).openFiles.size)
        assertEquals(f1.name, FileEditorManager.getInstance(project).openFiles.first().name)
    }

    fun `test open file path`() {
        val f1 = myFixture.addFileToProject("GoodbyeWorld.java", "")
        myFixture.addFileToProject("HelloWorld.java", "")

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalToolService>().blindExecute(
                OpenFileTool(), JsonObject().put("directive", "open src/goodbyeworld")
            )
            testContext.completeNow()
        }
        errorOnTimeout(testContext)

        assertEquals(1, FileEditorManager.getInstance(project).openFiles.size)
        assertEquals(f1.name, FileEditorManager.getInstance(project).openFiles.first().name)
    }
}
