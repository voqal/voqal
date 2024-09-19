package dev.voqal.assistant.context.code

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import io.vertx.core.json.JsonObject

interface ICode {
    val code: String?
    val language: String
    val filename: String?

    fun toJson(): JsonObject

    fun HighlightInfo.toJson(): JsonObject {
        return JsonObject().apply {
            put("description", description)
            put("severity", severity.toString())
            //put("line", highlighter.range)
        }
    }
}
