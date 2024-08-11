package dev.voqal.config

import io.vertx.core.json.JsonObject

interface ConfigurableSettings {

    fun toJson(): JsonObject
    fun withKeysRemoved(): ConfigurableSettings
    fun withPiiRemoved(): ConfigurableSettings
}
