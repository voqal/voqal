package dev.voqal.assistant.context

import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.config.settings.LanguageModelSettings
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.VoqalDirectiveService.Companion.convertJsonElementToMap
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter

/**
 * Represents the internal context of the Voqal Assistant.
 */
data class AssistantContext(
    val memorySlice: MemorySlice,
    val availableActions: Collection<VoqalTool>,
    val languageModelSettings: LanguageModelSettings,
    val promptSettings: PromptSettings? = null,
    val speechId: String? = null,
    val usingAudioModality: Boolean = false,
    val includeSystemPrompt: Boolean = true,
    var directiveMode: Boolean = promptSettings?.decomposeDirectives == true,
    var parentDirective: VoqalDirective? = null,
    val output: String? = null
) : VoqalContext {

    fun toJson(directive: VoqalDirective): JsonObject {
        return JsonObject().apply {
            put("includeToolsInMarkdown", promptSettings?.functionCalling == PromptSettings.FunctionCalling.MARKDOWN)
            put("availableTools", getVisibleTools(directive, availableActions))
            put("speechId", speechId)
            put("usingAudioModality", usingAudioModality)
            put("includeSystemPrompt", includeSystemPrompt)
            put("directiveMode", directiveMode)
            promptSettings?.let { put("promptSettings", it.toJson()) }
            parentDirective?.let { put("parentDirective", it.toJson()) }
            output?.let { put("output", it) }
        }
    }

    companion object {
        fun getVisibleTools(
            directive: VoqalDirective,
            availableActions: Collection<VoqalTool>
        ): List<Map<String, String>> {
            return availableActions
                .filter { it.isVisible(directive) }
                .map {
                    val tool = it.asTool(directive)
                    mapOf(
                        "name" to tool.function.name,
                        "yaml" to jsonToYaml(
                            Json.encodeToJsonElement(tool).toString(),
                            directive.assistant.directiveMode
                        )
                    )
                }
        }

        private fun jsonToYaml(jsonString: String, asDirective: Boolean = false): String {
            val jsonObject = JsonObject(jsonString)
            if (asDirective) {
                jsonObject.getJsonObject("function").remove("parameters")
                jsonObject.getJsonObject("function").put("parameters", JsonObject().apply {
                    put("type", "object")
                    put("properties", JsonObject().apply {
                        put("directive", JsonObject().apply {
                            put("type", "string")
                            put("description", "The directive to pass to the tool")
                        })
                    })
                })
            }
            val jsonElement = Json.parseToJsonElement(jsonObject.toString())
            val dataMap = convertJsonElementToMap(jsonElement)

            val options = DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                isPrettyFlow = true
            }

            val yaml = Yaml(options)
            val writer = StringWriter()
            yaml.dump(dataMap, writer)
            return writer.toString()
        }
    }
}
