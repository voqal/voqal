package dev.voqal.assistant.processing

import com.aallam.openai.api.chat.*
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.flaw.error.VoqalError
import dev.voqal.assistant.flaw.error.parse.ResponseParseError
import dev.voqal.assistant.tool.system.AnswerQuestionTool
import dev.voqal.assistant.tool.text.EditTextTool
import dev.voqal.services.VoqalToolService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.util.regex.Pattern

/**
 * Parses the wide variety of responses given by LLMs and returns the appropriate tool calls.
 */
object ResponseParser {

    //1|public class Test {
    //2|}
    private val lineNumberRegex = Regex("^\\d+\\|")

    fun parseEditMode(
        chunk: ChatCompletionChunk,
        directive: VoqalDirective
    ): VoqalResponse {
        return parseEditMode(
            ChatCompletion(
                id = chunk.id,
                created = chunk.created.toLong(),
                model = chunk.model,
                choices = chunk.choices.map { it.toChatChoice() },
                usage = chunk.usage,
                systemFingerprint = chunk.systemFingerprint
            ), directive, true
        )
    }

    private fun ChatChunk.toChatChoice(): ChatChoice {
        return ChatChoice(
            index = 0,
            message = ChatMessage(
                role = this.delta!!.role!!,
                messageContent = TextContent(this.delta!!.content!! + "\n```")
            )
        )
    }

    fun parseEditMode(
        completion: ChatCompletion,
        directive: VoqalDirective,
        streaming: Boolean = false
    ): VoqalResponse {
        val messageContent = completion.choices.firstOrNull()?.message?.messageContent
        val textContent = if (messageContent is TextContent) {
            messageContent.content
        } else {
            messageContent.toString()
        }
        val codeBlock = CodeExtractor.extractCodeBlock(textContent)

        //remove line numbers (if present)
        val hasLineNumbers = codeBlock.lines().let {
            //check without last line when streaming as we may have partial result
            if (streaming) it.dropLast(1) else it
        }.all { lineNumberRegex.containsMatchIn(it) }
        val codeBlockWithoutLineNumbers = if (hasLineNumbers) {
            codeBlock.lines().joinToString("\n") { it.replaceFirst(lineNumberRegex, "") }
        } else {
            codeBlock
        }

        return VoqalResponse(
            directive, listOf(
                ToolCall.Function(
                    id = ToolId(EditTextTool.NAME),
                    function = FunctionCall(
                        nameOrNull = EditTextTool.NAME,
                        argumentsOrNull = JsonObject().put("text", codeBlockWithoutLineNumbers).toString()
                    )
                )
            ), completion
        )
    }

    fun parse(
        completion: ChatCompletion,
        directive: VoqalDirective
    ): VoqalResponse {
        val log = directive.ide.project.getVoqalLogger(this::class)
        val message = completion.choices.first().message //todo: handle multiple choices
        log.debug("Chat completion response: ${message.content}")

        if (message.content != null) {
            if (message.toolCalls.isNullOrEmpty()) {
                //see if text content can be transformed into tool call
                try {
                    val pattern = Pattern.compile(
                        "###\\s*(.*?)\\s*\\n\\s*```json\\s*(\\{.*?\\})?\\s*```",
                        Pattern.DOTALL
                    )
                    val matcher = pattern.matcher(message.content)

                    if (matcher.find()) {
                        matcher.reset()

                        val toolCalls = mutableListOf<ToolCall.Function>()
                        while (matcher.find()) {
                            val toolName = matcher.group(1).replace("\\", "") //todo: replace all none alphanumeric_
                            var json = matcher.group(2)?.replace("\r\n", "")?.replace("\r", "") ?: "{}"

                            //try to parse json
                            try {
                                json = JsonObject(json).toString()
                            } catch (_: Exception) {
                                try {
                                    json = JsonObject(Json.decodeFromString<JsonElement>(json).toString()).toString()
                                } catch (_: Exception) {
                                }
                            }

                            toolCalls.add(
                                ToolCall.Function(
                                    id = ToolId(toolName),
                                    function = FunctionCall(
                                        nameOrNull = toolName,
                                        argumentsOrNull = json
                                    )
                                )
                            )
                        }
                        return VoqalResponse(directive, toolCalls, completion)
                    }

                    val json = try {
                        JsonObject(message.content)
                    } catch (_: Exception) {
                        JsonObject(CodeExtractor.extractCodeBlock(message.content!!))
                    }
                    if (json.size() == 1 && json.containsKey("answer")) {
                        val toolCalls = listOf(
                            ToolCall.Function(
                                id = ToolId(AnswerQuestionTool.NAME),
                                function = FunctionCall(
                                    nameOrNull = AnswerQuestionTool.NAME,
                                    argumentsOrNull = json.toString()
                                )
                            )
                        )
                        return VoqalResponse(directive, toolCalls, completion)
                    } else if (json.containsKey("tool") && json.containsKey("parameters")) {
                        val toolCalls = listOf(
                            ToolCall.Function(
                                id = ToolId(json.getString("tool")),
                                function = FunctionCall(
                                    nameOrNull = json.getString("tool"),
                                    argumentsOrNull = json.getJsonObject("parameters").toString()
                                )
                            )
                        )
                        return VoqalResponse(directive, toolCalls, completion)
                    } else {
                        if (json.map.keys.size == 1 && directive.assistant.availableActions.any { it.name == json.map.keys.first() }) {
                            val toolName = json.map.keys.first()
                            val toolJson = json.getJsonObject(toolName).toString()
                            val toolCalls = listOf(
                                ToolCall.Function(
                                    id = ToolId(toolName),
                                    function = FunctionCall(
                                        nameOrNull = toolName,
                                        argumentsOrNull = toolJson
                                    )
                                )
                            )
                            return VoqalResponse(directive, toolCalls, completion)
                        } else if (directive.assistant.availableActions.size == 1) {
                            val toolName = directive.assistant.availableActions.first().name
                            val toolJson = json.toString()
                            val toolCalls = listOf(
                                ToolCall.Function(
                                    id = ToolId(toolName),
                                    function = FunctionCall(
                                        nameOrNull = toolName,
                                        argumentsOrNull = toolJson
                                    )
                                )
                            )
                            return VoqalResponse(directive, toolCalls, completion)
                        } else {
                            val jsonBlockRegex = Pattern.compile("```json(.*?)```", Pattern.DOTALL)
                            val matcher = jsonBlockRegex.matcher(message.content)
                            if (matcher.find()) {
                                //extract all json blocks
                                val toolCalls = mutableListOf<ToolCall.Function>()
                                do {
                                    val jsonBlock = matcher.group(1)
                                    val json = try {
                                        JsonObject(jsonBlock)
                                    } catch (_: Exception) {
                                        JsonObject(CodeExtractor.extractCodeBlock(jsonBlock))
                                    }
                                    if (json.containsKey("name") && json.containsKey("parameters")) {
                                        toolCalls.add(
                                            ToolCall.Function(
                                                id = ToolId(json.getString("name")),
                                                function = FunctionCall(
                                                    nameOrNull = json.getString("name"),
                                                    argumentsOrNull = json.getJsonObject("parameters").toString()
                                                )
                                            )
                                        )
                                    } else {
                                        throw ResponseParseError(
                                            completion,
                                            "Unable to find tool: ${json.map.keys.first()}"
                                        )
                                    }
                                } while (matcher.find())

                                return VoqalResponse(directive, toolCalls, completion)
                            }

                            //todo: can save custom property to helicone for easier tracking
                            throw ResponseParseError(
                                completion,
                                "Unable to find tool: ${json.map.keys.first()}"
                            )
                        }
                    }
                } catch (e: VoqalError) {
                    throw e
                } catch (_: Exception) {
                    if (looksLikeRawDirectiveArray(message.content!!)) {
                        val jsonArray = try {
                            JsonArray(message.content)
                        } catch (_: Exception) {
                            JsonArray(CodeExtractor.extractCodeBlock(message.content!!))
                        }
                        val toolCalls = jsonArray.map {
                            val tool = it as JsonObject
                            val name = tool.fieldNames().first()
                            ToolCall.Function(
                                id = ToolId(name),
                                function = FunctionCall(
                                    nameOrNull = name,
                                    argumentsOrNull = tool.getJsonObject(name).toString()
                                )
                            )
                        }
                        return VoqalResponse(directive, toolCalls, completion)
                    } else if (looksLikeRawAnswerJson(message.content!!)) {
                        val rawJson = message.content!!.substringAfter("```json").substringBefore("```")
                            .replace("\\r\\n", "\n")
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"")
                        try {
                            val jsonObject = JsonObject(rawJson)
                            val toolCalls = listOf(
                                ToolCall.Function(
                                    id = ToolId(AnswerQuestionTool.NAME),
                                    function = FunctionCall(
                                        nameOrNull = AnswerQuestionTool.NAME,
                                        argumentsOrNull = JsonObject().apply {
                                            put("answer", jsonObject.getString("answer"))
                                        }.toString()
                                    )
                                )
                            )
                            return VoqalResponse(directive, toolCalls, completion)
                        } catch (_: Exception) {
                            try {
                                val jsonArray = JsonArray(rawJson)
                                val toolCalls = jsonArray.map {
                                    val tool = it as JsonObject
                                    val name = tool.fieldNames().first()
                                    ToolCall.Function(
                                        id = ToolId(name),
                                        function = FunctionCall(
                                            nameOrNull = name,
                                            argumentsOrNull = tool.getJsonObject(name).toString()
                                        )
                                    )
                                }
                                return VoqalResponse(directive, toolCalls, completion)
                            } catch (e: Exception) {
                                throw ResponseParseError(
                                    completion,
                                    e.message ?: "Failed to parse language model response"
                                )
                            }
                        }
                    } else if (looksLikeRawAnswerYaml(message.content!!)) {
                        try {
                            val rawYaml = message.content!!.substringAfter("```yaml").substringBefore("```")
                                .replace("\\r\\n", "\n")
                                .replace("\\n", "\n")
                                .replace("\\\"", "\"")
                            val parse = VoqalToolService.parseYaml(rawYaml)
                            val toolCalls = listOf(
                                ToolCall.Function(
                                    id = ToolId(AnswerQuestionTool.NAME),
                                    function = FunctionCall(
                                        nameOrNull = AnswerQuestionTool.NAME,
                                        argumentsOrNull = JsonObject().apply {
                                            put("answer", parse["answer"])
                                        }.toString()
                                    )
                                )
                            )
                            return VoqalResponse(directive, toolCalls, completion)
                        } catch (_: Exception) {
                        }
                    } else if (looksLikeAnswerQuestion(message.content!!)) {
                        val toolCalls = listOf(
                            ToolCall.Function(
                                id = ToolId(AnswerQuestionTool.NAME),
                                function = FunctionCall(
                                    nameOrNull = AnswerQuestionTool.NAME,
                                    argumentsOrNull = JsonObject().apply {
                                        put("answer", message.content)
                                    }.toString()
                                )
                            )
                        )
                        return VoqalResponse(directive, toolCalls, completion)
                    } else {
                        throw ResponseParseError(
                            completion,
                            "Failed to parse language model response"
                        )
                    }
                }
            } else {
                log.warn("Got message content and tool calls. Dropping tool calls and using message content as answer")
                val toolCalls = listOf(
                    ToolCall.Function(
                        id = (message.toolCalls!!.first() as ToolCall.Function).id,
                        function = FunctionCall(
                            nameOrNull = AnswerQuestionTool.NAME,
                            argumentsOrNull = message.content
                        )
                    )
                )
                return VoqalResponse(directive, toolCalls, completion)
            }
        }

        return VoqalResponse(directive, message.toolCalls ?: emptyList(), completion)
    }

    private fun looksLikeAnswerQuestion(input: String): Boolean {
        val hasVowels = input.any { it in "aeiouAEIOU" }
        val hasSpaces = input.contains(" ")
        val hasMultipleWords = input.split("\\s+".toRegex()).size > 1
        return hasVowels && hasSpaces && hasMultipleWords
    }

    private fun looksLikeRawAnswerYaml(input: String): Boolean {
        if (!input.startsWith("```yaml") || !input.endsWith("```")) {
            return false
        } else if (!input.contains("answer:")) {
            return false
        }
        return true
    }

    private fun looksLikeRawAnswerJson(input: String): Boolean {
        return input.startsWith("```json")
    }

    private fun looksLikeRawDirectiveArray(input: String): Boolean {
        return try {
            JsonArray(input).toString().contains("directive")
        } catch (_: Exception) {
            try {
                JsonArray(CodeExtractor.extractCodeBlock(input)).toString().contains("directive")
            } catch (_: Exception) {
                false
            }
        }
    }
}
