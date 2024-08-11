package dev.voqal.assistant.context.code

import dev.voqal.assistant.context.VoqalContext
import io.vertx.core.json.JsonObject

data class SelectedCode(
    override val code: String,
    val startIndex: Int,
    val endIndex: Int,
    override val language: String = "",
    override val filename: String? = null
) : VoqalContext, ICode {

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("code", code)
            put("startIndex", startIndex)
            put("endIndex", endIndex)
            put("language", language)
            put("filename", filename)
        }
    }
}
