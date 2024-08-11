package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import io.vertx.core.json.JsonObject

data class LanguageModelsSettings(
    val models: List<LanguageModelSettings> = emptyList()
) : ConfigurableSettings {

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        models = json.getJsonArray("models")?.map { LanguageModelSettings(it as JsonObject) } ?: emptyList()
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("models", models.map { it.toJson() })
        }
    }

    override fun withKeysRemoved(): LanguageModelsSettings {
        return copy(
            models = models.map { it.withKeysRemoved() }
        )
    }

    override fun withPiiRemoved(): LanguageModelsSettings {
        return withKeysRemoved().copy(
            models = models.map { it.withPiiRemoved() }
        )
    }
}
