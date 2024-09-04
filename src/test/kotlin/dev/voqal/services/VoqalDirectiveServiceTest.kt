package dev.voqal.services

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.intellij.openapi.components.service
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.tool.ide.navigation.OpenFileTool
import dev.voqal.config.settings.LanguageModelSettings
import dev.voqal.config.settings.PromptSettings
import dev.voqal.provider.LlmProvider
import dev.voqal.provider.clients.AiProvidersClient
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joor.Reflect
import org.mockito.kotlin.mock

class VoqalDirectiveServiceTest : JBTest() {

    fun `test retry on parse error`(): Unit = runBlocking {
        myFixture.addFileToProject("UserProfile.java", "")
        myFixture.addFileToProject("UserManagement.java", "")
        myFixture.addFileToProject("UserAccount.java", "")

        var sentErrorMessage = false
        val mockLlmProvider = mock<LlmProvider>(
            defaultAnswer = {
                if (it.method.name == "getName") {
                    return@mock "mock"
                } else if (it.method.name == "dispose") {
                    return@mock Unit
                }

                if (it.method.name == "chatCompletion" && !sentErrorMessage) {
                    sentErrorMessage = true
                    return@mock ChatCompletion(
                        id = "n/a",
                        created = System.currentTimeMillis(),
                        model = ModelId("n/a"),
                        choices = listOf(
                            ChatChoice(
                                index = 0,
                                ChatMessage(
                                    ChatRole.Assistant,
                                    TextContent(
                                        content = "```json\n" +
                                                "{\n" +
                                                "  \"name\": \"user management\"\n" +
                                                "}\n" +
                                                "```"
                                    )
                                )
                            )
                        )
                    )
                } else if (it.method.name == "chatCompletion") {
                    return@mock ChatCompletion(
                        id = "n/a",
                        created = System.currentTimeMillis(),
                        model = ModelId("n/a"),
                        choices = listOf(
                            ChatChoice(
                                index = 0,
                                ChatMessage(
                                    ChatRole.Assistant,
                                    TextContent(
                                        content = "```json\n" +
                                                "{\n" +
                                                " \"name\": \"open_file\",\n" +
                                                " \"parameters\": {\n" +
                                                "  \"name\": \"user management\"\n" +
                                                " }\n" +
                                                "}\n" +
                                                "```"
                                    )
                                )
                            )
                        )
                    )
                }
                throw NotImplementedError("Method not implemented: ${it.method.name}")
            }
        )
        val aiProvidersClient = project.service<VoqalConfigService>().getAiProvider() as AiProvidersClient
        //use reflect to skip Disposer.register
        Reflect.on(aiProvidersClient).get<MutableList<LlmProvider>>("llmProviders").add(mockLlmProvider)

        val testContext = VertxTestContext()
        project.service<VoqalDirectiveService>().onDirectiveExecution { _, directiveExecution ->
            if (directiveExecution.response == null) return@onDirectiveExecution
            testContext.verify {
                assertEquals(1, directiveExecution.response!!.toolCalls.size)
                val toolCall = directiveExecution.response!!.toolCalls[0] as ToolCall.Function
                val functionCall = toolCall.function
                assertEquals(OpenFileTool.NAME, functionCall.name)
                assertEquals("user management", JsonObject(functionCall.arguments).getString("name"))
                testContext.completeNow()
            }
        }

        val toolService = project.service<VoqalToolService>()
        val transcription = "Open the user management file"
        val directive = VoqalDirective(
            assistant = AssistantContext(
                memorySlice = getMemorySystem().getMemorySlice(),
                availableActions = toolService.getAvailableTools().values,
                promptSettings = PromptSettings(promptName = "Idle Mode"),
                languageModelSettings = LanguageModelSettings(name = "mock")
            ),
            ide = IdeContext(project),
            developer = DeveloperContext(transcription = transcription)
        )

        project.scope.launch {
            project.service<VoqalDirectiveService>().executeDirective(directive)
        }
        errorOnTimeout(testContext)
    }
}
