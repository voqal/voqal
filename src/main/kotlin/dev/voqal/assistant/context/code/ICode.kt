package dev.voqal.assistant.context.code

import io.vertx.core.json.JsonObject

interface ICode {
    val code: String?
    val language: String
    val filename: String?

    fun toJson(): JsonObject
}
