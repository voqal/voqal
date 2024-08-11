package dev.voqal.assistant.processing

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.InternalContext
import dev.voqal.assistant.flaw.error.parse.ResponseParseError
import dev.voqal.assistant.memory.local.LocalMemorySlice
import dev.voqal.assistant.tool.code.CreateClassTool
import dev.voqal.assistant.tool.ide.navigation.GotoTextTool
import dev.voqal.assistant.tool.system.AnswerQuestionTool
import io.vertx.core.json.JsonObject
import org.mockito.kotlin.mock

class VoqalResponseParserTest : JBTest() {

    fun `test json seperated tools using parameters field`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
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
                                        "  \"name\": \"test1\"\n" +
                                        " }\n" +
                                        "}\n" +
                                        "```\n" +
                                        "\n" +
                                        "```json\n" +
                                        "{\n" +
                                        " \"name\": \"open_file\",\n" +
                                        " \"parameters\": {\n" +
                                        "  \"name\": \"test2\"\n" +
                                        " }\n" +
                                        "}\n" +
                                        "```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(2, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("open_file", functionCall1.id.id)
        val functionCallJson1 = JsonObject(functionCall1.function.arguments)
        assertEquals("test1", functionCallJson1.getString("name"))

        val toolCall2 = response.toolCalls[1]
        assertTrue(toolCall2 is ToolCall.Function)
        val functionCall2 = toolCall2 as ToolCall.Function
        assertEquals("open_file", functionCall2.id.id)
        val functionCallJson2 = JsonObject(functionCall2.function.arguments)
        assertEquals("test2", functionCallJson2.getString("name"))
    }

    fun `test single tool with only args given`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
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
                                        "  \"text\": \"response\"\n" +
                                        "}\n" +
                                        "```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = InternalContext(
                    memorySlice = mock {},
                    availableActions = listOf(GotoTextTool()),
                    languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
                ),
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("goto_text", functionCall1.id.id)
        val functionCallJson1 = JsonObject(functionCall1.function.arguments)
        assertEquals("response", functionCallJson1.getString("text"))
    }

    fun `test code block directive array`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "```\n[\n  {\n    \"run_program\": {\n      \"directive\": \"Test.java\"\n    }\n  }\n]\n```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("run_program", functionCall1.id.id)
        val functionCallJson1 = JsonObject(functionCall1.function.arguments)
        assertEquals("Test.java", functionCallJson1.getString("directive"))
    }

    fun `test empty json block`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = """
                                    ### tool_1
                                    ```json
                                    {
                                    }
                                    ```

                                    ### tool_2
                                    ```json
                                    {}
                                    ```

                                    ### tool_3
                                    ```json
                                    ```
                                """.trimIndent()
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(3, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("tool_1", functionCall1.id.id)
        val functionCallJson1 = JsonObject(functionCall1.function.arguments)
        assertTrue(functionCallJson1.isEmpty)

        val toolCall2 = response.toolCalls[1]
        assertTrue(toolCall2 is ToolCall.Function)
        val functionCall2 = toolCall2 as ToolCall.Function
        assertEquals("tool_2", functionCall2.id.id)
        val functionCallJson2 = JsonObject(functionCall2.function.arguments)
        assertTrue(functionCallJson2.isEmpty)

        val toolCall3 = response.toolCalls[2]
        assertTrue(toolCall3 is ToolCall.Function)
        val functionCall3 = toolCall3 as ToolCall.Function
        assertEquals("tool_3", functionCall3.id.id)
        val functionCallJson3 = JsonObject(functionCall3.function.arguments)
        assertTrue(functionCallJson3.isEmpty)
    }

    fun `test parse regular text content with new line`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "Hello! How can I assist you today?\n"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals("Hello! How can I assist you today?\n", functionCallJson.getString("answer"))
    }

    fun `test parse regular text content with multiple line breaks`() {
        val message = """
It appears that the developer's transcription consists solely of the word "test" with no context or further instruction. This transcription does not provide enough information to determine a specific course of action or tool usage. 

Without additional information or a clear request from the developer, no action can be taken.

If the developer intends to test the system, they may provide a specific task or voice a command for action.
        """.trimIndent()
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = message
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals(message, functionCallJson.getString("answer"))
    }

    fun `test parse json tool request`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "{\n" +
                                        "  \"create_class\": {\n" +
                                        "    \"class_name\": \"calculator\",\n" +
                                        "    \"language\": \"java\"\n" +
                                        "  }\n" +
                                        "}"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = InternalContext(
                    memorySlice = mock {},
                    availableActions = listOf(CreateClassTool()),
                    languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
                ),
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(CreateClassTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals("calculator", functionCallJson.getString("class_name"))
        assertEquals("java", functionCallJson.getString("language"))
    }

    fun `test parse json tool request - tool and parameters`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "{\n" +
                                        "  \"tool\": \"answer_question\",\n" +
                                        "  \"parameters\": {\n" +
                                        "    \"answer\": \"You have the 'multiply' function selected.\"\n" +
                                        "  }\n" +
                                        "}"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = InternalContext(
                    memorySlice = mock {},
                    availableActions = emptyList(),
                    languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
                ),
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals("You have the 'multiply' function selected.", functionCallJson.getString("answer"))
    }

    fun `test parse json tool request for unavailable tool`() {
        try {
            VoqalResponseParser.parse(
                ChatCompletion(
                    id = "n/a",
                    created = System.currentTimeMillis(),
                    model = ModelId("n/a"),
                    choices = listOf(
                        ChatChoice(
                            index = 0,
                            ChatMessage(
                                ChatRole.Assistant,
                                TextContent(
                                    content = "{\n" +
                                            "  \"create_new_class\": {\n" +
                                            "    \"class_name\": \"calculator\",\n" +
                                            "    \"language\": \"java\"\n" +
                                            "  }\n" +
                                            "}"
                                )
                            )
                        )
                    )
                ),
                VoqalDirective(
                    ide = IdeContext(project, mock {}),
                    internal = mock {},
                    developer = mock {},
                )
            )
            fail()
        } catch (e: Exception) {
            assertTrue(e is ResponseParseError)
        }
    }

    fun `test parse raw text content as answer question`() {
        val rawAnswer =
            "I'm not sure what specific task you'd like me to assist with. Please provide more details so I can help you effectively."
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = rawAnswer
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals(rawAnswer, functionCallJson.getString("answer"))
    }

    fun `test parse raw text content as yaml as answer question`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "```yaml\\nanswer: \\\"I'm not your friend, buddy\\\"\\n```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals("I'm not your friend, buddy", functionCallJson.getString("answer"))
    }

    fun `test parse raw text content as json as answer question`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "```json\\r\\n{\\r\\n  \\\"answer\\\": \\\"I'm sorry, but I'm unable to provide the current time.\\\"\\r\\n}\\r\\n```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)
        val toolCall = response.toolCalls.first()
        assertTrue(toolCall is ToolCall.Function)
        val functionCall = toolCall as ToolCall.Function
        assertEquals(AnswerQuestionTool.NAME, functionCall.id.id)
        val functionCallJson = JsonObject(functionCall.function.arguments)
        assertEquals(
            "I'm sorry, but I'm unable to provide the current time.",
            functionCallJson.getString("answer")
        )
    }

    fun `test directive array`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "[\n  {\n    \"open_file\": {\n      \"directive\": \"copy.py\"\n    }\n  },\n  {\n    \"view_source\": {\n      \"directive\": \"copy.py\"\n    }\n  },\n  {\n    \"answer_question\": {\n      \"directive\": \"What does copy.py do?\"\n    }\n  }\n]"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(3, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("open_file", functionCall1.id.id)

        val toolCall2 = response.toolCalls[1]
        assertTrue(toolCall2 is ToolCall.Function)
        val functionCall2 = toolCall2 as ToolCall.Function
        assertEquals("view_source", functionCall2.id.id)

        val toolCall3 = response.toolCalls[2]
        assertTrue(toolCall3 is ToolCall.Function)
        val functionCall3 = toolCall3 as ToolCall.Function
        assertEquals("answer_question", functionCall3.id.id)
    }

    fun `test directive array2`() {
        val response = VoqalResponseParser.parse(
            ChatCompletion(
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
                                        "[\n" +
                                        "  {\n" +
                                        "    \"open_file\": {\n" +
                                        "      \"directive\": \"Main.java\"\n" +
                                        "    }\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"view_source\": {\n" +
                                        "      \"directive\": \"src/Main.java\"\n" +
                                        "    }\n" +
                                        "  }\n" +
                                        "]\n" +
                                        "```"
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = mock {},
                developer = mock {},
            )
        )
        assertEquals(2, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("open_file", functionCall1.id.id)

        val toolCall2 = response.toolCalls[1]
        assertTrue(toolCall2 is ToolCall.Function)
        val functionCall2 = toolCall2 as ToolCall.Function
        assertEquals("view_source", functionCall2.id.id)
    }

    fun `test directive array3 error`() {
        val test =
            JsonObject("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"```json\\n[\\n  {\\n    \\\"answer_question\\\": {\\n      \\\"directive\\\": \\\"What does 'interpreter.chat(\\\\\\\"Hello!\\\\\\\", blocking=False)' go to?\\\"\\n    }\\n  },\\n  {\\n    \\\"view_source\\\": {\\n      \\\"directive\\\": \\\"copy.py\\\"\\n    }\\n  }\\n]\\n```\"}],\"role\":\"model\"},\"finishReason\":\"STOP\",\"index\":0,\"safetyRatings\":[{\"category\":\"HARM_CATEGORY_SEXUALLY_EXPLICIT\",\"probability\":\"NEGLIGIBLE\"},{\"category\":\"HARM_CATEGORY_HATE_SPEECH\",\"probability\":\"NEGLIGIBLE\"},{\"category\":\"HARM_CATEGORY_HARASSMENT\",\"probability\":\"NEGLIGIBLE\"},{\"category\":\"HARM_CATEGORY_DANGEROUS_CONTENT\",\"probability\":\"NEGLIGIBLE\"}]}],\"usageMetadata\":{\"promptTokenCount\":9579,\"candidatesTokenCount\":75,\"totalTokenCount\":9654}}")
        try {
            VoqalResponseParser.parse(
                ChatCompletion(
                    id = "n/a",
                    created = System.currentTimeMillis(),
                    model = ModelId("n/a"),
                    choices = listOf(
                        ChatChoice(
                            index = 0,
                            ChatMessage(
                                ChatRole.Assistant,
                                TextContent(
                                    content = ((test.getJsonArray("candidates")
                                        .first() as JsonObject).getJsonObject("content").getJsonArray("parts")
                                        .first() as JsonObject).getString("text")
                                )
                            )
                        )
                    )
                ),
                VoqalDirective(
                    ide = IdeContext(project, mock {}),
                    internal = InternalContext(
                        memorySlice = LocalMemorySlice(project),
                        availableActions = emptyList(),
                        languageModelSettings = mock {}
                    ),
                    developer = mock {},
                )
            )
        } catch (e: Exception) {
            assertTrue(e.message, e is ResponseParseError)
        }
    }

    fun `test edit code with extra data`() {
        val test =
            JsonObject("{\"content\": \"```kotlin\\nclass TestClass {\\n    fun add(firstParam: Int, y: Int): Int {\\n        return firstParam + y\\n    }\\n}\\n```\\n\\n```json\\n{\\\"accept\\\": true}\\n```\\n\\nNote: The output is the modified code based on the developer's request to rename the parameter `x` to `firstParam`.\"}")
        val response = VoqalResponseParser.parseEditMode(
            ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = test.getString("content")
                            )
                        )
                    )
                )
            ),
            VoqalDirective(
                ide = IdeContext(project, mock {}),
                internal = InternalContext(
                    memorySlice = LocalMemorySlice(project),
                    availableActions = emptyList(),
                    languageModelSettings = mock {}
                ),
                developer = mock {},
            )
        )
        assertEquals(1, response.toolCalls.size)

        val toolCall1 = response.toolCalls[0]
        assertTrue(toolCall1 is ToolCall.Function)
        val functionCall1 = toolCall1 as ToolCall.Function
        assertEquals("edit_text", functionCall1.id.id)

        val functionCallJson = JsonObject(functionCall1.function.arguments)
        assertEquals(
            "class TestClass {\n" +
                    "    fun add(firstParam: Int, y: Int): Int {\n" +
                    "        return firstParam + y\n" +
                    "    }\n" +
                    "}",
            functionCallJson.getString("text")
        )
    }
}
