package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.InternalContext
import dev.voqal.assistant.processing.VoqalResponseParser
import io.vertx.core.json.JsonObject
import org.mockito.kotlin.mock

class AnswerQuestionToolTest : JBTest() {

    fun testParseNewLinesInAnswer() {
        val json =
            "{\"content\" : \"## Assistant Tool Request\\n\\n### answer_question\\n\\n```json\\n{\\n  \\\"answer\\\": \\\"I can assist you with a variety of tasks. Here's a quick overview of what I can do: \\n  - Run, debug, stop, and control the execution of programs\\n  - Open, close, scroll, and navigate files and tabs\\n  - Show, hide, and focus on different code and tool windows\\n  - Create classes, functions, and other code structures\\n  - Add and remove breakpoints\\n  - Perform code editing like backspacing, deleting, undoing, redoing, selecting text, and more\\n  - Dictate text and code\\n  - Answer your questions and provide code refactoring\\n\\nFeel free to ask for specifics, and I’ll be happy to help!\\\"\\n}\\n```\"}"
        val completion = ChatCompletion(
            id = "n/a",
            created = System.currentTimeMillis(),
            model = ModelId("n/a"),
            choices = listOf(
                ChatChoice(
                    index = 0,
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(JsonObject(json).getString("content"))
                    )
                )
            )
        )
        val directive = VoqalDirective(
            ide = IdeContext(project),
            internal = InternalContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            developer = DeveloperContext(
                transcription = ""
            )
        )
        val response = VoqalResponseParser.parse(completion, directive)
        assertTrue(JsonObject((response.toolCalls.first() as ToolCall.Function).function.arguments).containsKey("answer"))
    }

    fun testJsonOnlyAnswer() {
        val content = " ```json\\n{\\n  \\\"answer\\\": \\\"Hello! How can I assist you today?\\\"\\n}\\n```"
        val completion = ChatCompletion(
            id = "n/a",
            created = System.currentTimeMillis(),
            model = ModelId("n/a"),
            choices = listOf(
                ChatChoice(
                    index = 0,
                    ChatMessage(
                        ChatRole.Assistant,
                        TextContent(content)
                    )
                )
            )
        )
        val directive = VoqalDirective(
            ide = IdeContext(project),
            internal = InternalContext(
                memorySlice = mock {},
                availableActions = emptyList(),
                languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first()
            ),
            developer = DeveloperContext(
                transcription = ""
            )
        )
        val response = VoqalResponseParser.parse(completion, directive)
        assertEquals(
            "Hello! How can I assist you today?",
            JsonObject((response.toolCalls.first() as ToolCall.Function).function.arguments).getString("answer")
        )
    }
}
