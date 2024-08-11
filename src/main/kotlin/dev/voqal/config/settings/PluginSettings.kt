package dev.voqal.config.settings

import dev.voqal.config.ConfigurableSettings
import io.vertx.core.json.JsonObject

data class PluginSettings(
    val enabled: Boolean = true,
    val microphoneName: String = "",
    val pauseOnFocusLost: Boolean = true
) : ConfigurableSettings {

    /**
     * Need to set defaults so config changes don't reset stored config due to parse error.
     */
    constructor(json: JsonObject) : this(
        enabled = json.getBoolean("enabled", true),
        microphoneName = json.getString("microphoneName", ""),
        pauseOnFocusLost = json.getBoolean("pauseOnFocusLost", true)
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("enabled", enabled)
            put("microphoneName", microphoneName)
            put("pauseOnFocusLost", pauseOnFocusLost)
        }
    }

    override fun withKeysRemoved(): PluginSettings {
        return copy()
    }

    override fun withPiiRemoved(): PluginSettings {
        return withKeysRemoved().copy(microphoneName = if (microphoneName.isEmpty()) "" else "***")
    }
}
