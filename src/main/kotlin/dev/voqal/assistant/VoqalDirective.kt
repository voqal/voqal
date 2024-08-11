package dev.voqal.assistant

import com.intellij.openapi.components.service
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.InternalContext
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
 * @property ide Holds the current state of the IDE.
 * @property internal Holds the current configuration of the Voqal Assistant.
 * @property developer Holds information provided by developer.
 */
data class VoqalDirective(
    val ide: IdeContext,
    val internal: InternalContext,
    val developer: DeveloperContext
) {

    val requestId by lazy { internal.memorySlice.id }
    val directiveId by lazy { internal.parentDirective?.internal?.memorySlice?.id ?: internal.memorySlice.id }
    val project = ide.project

    fun toMarkdown(): String {
        val promptSettings = internal.promptSettings ?: throw IllegalStateException("Prompt settings not found.")
        val promptTemplate = ide.project.service<VoqalConfigService>().getPromptTemplate(promptSettings)
        val compiledTemplate = VoqalTemplateEngine.getTemplate(promptTemplate)
        val writer = StringWriter()

        val internalMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(internal.toJson(this).toString())
        )
        val developerMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(developer.toJson().toString())
        )
        val ideMap = VoqalDirectiveService.convertJsonElementToMap(
            Json.parseToJsonElement(ide.toJson().toString())
        )
        val contextMap = mutableMapOf(
            "assistant" to internalMap,
            "developer" to developerMap,
            "ide" to ideMap,
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
            put("assistant", internal.toJson(this@VoqalDirective))
            put("developer", developer.toJson())
            put("ide", ide.toJson())
        }
    }

    fun getLanguageModelSettings(): LanguageModelSettings {
        return internal.languageModelSettings
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
