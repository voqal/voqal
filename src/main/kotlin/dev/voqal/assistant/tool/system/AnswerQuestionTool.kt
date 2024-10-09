package dev.voqal.assistant.tool.system

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class AnswerQuestionTool : VoqalTool() {

    companion object {
        const val NAME = "answer_question"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val answer = args.getString("answer")
        if (answer == null) {
            //sometimes llm sends null answer instead of ignore
            log.info("Ignoring null answer")
            project.service<VoqalStatusService>().updateText("Ignoring null answer")
            return
        }

        val textOnly = (directive.assistant.parentDirective?.developer?.textOnly
            ?: directive.developer.textOnly) || args.getBoolean("audioModality") == true
        project.service<VoqalDirectiveService>().handleResponse(answer, isTextOnly = textOnly)
    }

    override fun supportsDirectiveMode(): Boolean {
        return true
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            if (directive.assistant.directiveMode) {
                append("Use this tool to answer the developer's question. ")
                append("Change the directive to a question if it is not already. ")
                append("The question should be from the developer's perspective. Not the assistant's. ")
                append("Use this tool to answer greetings, questions, or any other general conversation. ")
                append("Default to asking a question via this tool or " + IgnoreTool.NAME + ".")
            } else {
                append("Use this tool whenever the developer asks you a question. ")
                append("If the developer's prompt has a question mark in it, you should obviously use this tool. ")
                append("Never call this tool with raw code. Remember, you are a voice assistant. Be succinct and to the point. ")
                append("Use this tool to answer greetings, questions, or any other general conversation. ")
                append("Default to asking a question via this tool or " + IgnoreTool.NAME + ".")
            }
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                if (directive.assistant.directiveMode) {
                    put("directive", JsonObject().apply {
                        put("type", "string")
                        put("description", "The directive to pass to the tool. Must be in the form of a question")
                    })
                } else {
                    put("answer", JsonObject().apply {
                        put("type", "string")
                        put("description", "The answer to TTS to the developer.")
                    })
                }
            })
            if (directive.assistant.directiveMode) {
                put("required", JsonArray().add("directive"))
            } else {
                put("required", JsonArray().add("answer"))
            }
        }.toString())
    )
}
