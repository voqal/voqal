package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import io.vertx.core.json.JsonObject

data class PromptLibrarySettings(
    val prompts: List<PromptSettings> = DEFAULT_PROMPTS
) : ConfigurableSettings {

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        prompts = json.getJsonArray("prompts")?.map { PromptSettings(it as JsonObject) } ?: DEFAULT_PROMPTS
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("prompts", prompts.map { it.toJson() })
        }
    }

    override fun withKeysRemoved(): PromptLibrarySettings {
        return copy(
            prompts = prompts.map { it.withKeysRemoved() }
        )
    }

    override fun withPiiRemoved(): PromptLibrarySettings {
        return withKeysRemoved().copy(
            prompts = prompts.map { it.withPiiRemoved() }
        )
    }

    companion object {
        @JvmStatic
        val DEFAULT_PROMPTS = listOf(
            PromptSettings(promptName = "Edit Mode", codeSmellCorrection = true),
            PromptSettings(promptName = "Idle Mode", decomposeDirectives = true),
            PromptSettings(promptName = "Search Mode")
        )
    }
}
