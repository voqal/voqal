package dev.voqal.assistant

import com.intellij.openapi.components.service
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.template.VoqalTemplateEngine
import dev.voqal.config.settings.LanguageModelSettings
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.VoqalDirectiveService
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json
import java.io.StringWriter

/**
 * Represents a processable command for the Voqal Assistant.
 *
 * @property assistant Holds the current configuration of the assistant.
 * @property ide Holds the current state of the IDE.
 * @property developer Holds information provided by developer.
 */
data class VoqalDirective(
    val assistant: AssistantContext,
    val ide: IdeContext,
    val developer: DeveloperContext
) {

    val requestId by lazy { assistant.memorySlice.id }
    val directiveId by lazy { assistant.parentDirective?.assistant?.memorySlice?.id ?: assistant.memorySlice.id }
    val project = ide.project

    fun toMarkdown(): String {
        val promptSettings = assistant.promptSettings ?: throw IllegalStateException("Prompt settings not found")
        val promptTemplate = ide.project.service<VoqalConfigService>().getPromptTemplate(promptSettings)
        val compiledTemplate = VoqalTemplateEngine.getTemplate(promptTemplate)
        val writer = StringWriter()

        val assistantMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(assistant.toJson(this).toString())
        )
        val ideMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(ide.toJson().toString())
        )
        val developerMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(developer.toJson().toString())
        )
        val contextMap = mutableMapOf(
            "assistant" to assistantMap,
            "ide" to ideMap,
            "developer" to developerMap,
            "directive" to this
        )
        compiledTemplate.evaluate(writer, contextMap)
        val fullPrompt = writer.toString()

        //merge empty new lines into single new line (ignoring code blocks)
        val finalPrompt = StringWriter()
        val fullPromptLines = fullPrompt.lines()
        var inCodeBlock = false
        var previousLineBlank = false
        fullPromptLines.forEachIndexed { index, line ->
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock
                finalPrompt.appendLine(line)
                previousLineBlank = false
                return@forEachIndexed
            }

            if (index == 0 && line.isBlank()) {
                previousLineBlank = true
            } else if (inCodeBlock) {
                finalPrompt.appendLine(line)
                previousLineBlank = false
            } else {
                if (line.isBlank()) {
                    if (!previousLineBlank) {
                        finalPrompt.appendLine(line)
                        previousLineBlank = true
                    }
                } else {
                    finalPrompt.appendLine(line)
                    previousLineBlank = false
                }
            }
        }
        return finalPrompt.toString()
    }

    fun toJson(): JsonObject {
        return JsonObject().apply {
            put("assistant", assistant.toJson(this@VoqalDirective))
            put("ide", ide.toJson())
            put("developer", developer.toJson())
        }
    }

    fun getLanguageModelSettings(): LanguageModelSettings {
        return assistant.languageModelSettings
    }

    override fun hashCode(): Int {
        return requestId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        return requestId == (other as VoqalDirective).requestId
    }
}
