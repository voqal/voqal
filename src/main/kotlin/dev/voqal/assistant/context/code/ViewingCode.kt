package dev.voqal.assistant.context.code

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.Caret
import dev.voqal.assistant.context.VoqalContext
import io.vertx.core.json.JsonObject

data class ViewingCode(
    override val code: String? = null,
    val codeWithLineNumbers: String? = code?.withLineNumbers(),
    override val language: String = "",
    override val filename: String? = null,
    val problems: List<HighlightInfo> = emptyList()
) : VoqalContext, ICode {

    constructor(json: JsonObject) : this(
        code = json.getString("code"),
        codeWithLineNumbers = json.getString("codeWithLineNumbers"),
        language = json.getString("language"),
        filename = json.getString("filename")
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("code", code)
            put("codeWithLineNumbers", codeWithLineNumbers)
            put("language", language)
            put("filename", filename)

            if (problems.isNotEmpty()) {
                put("problems", problems.map { it.toJson() })
            }
        }
    }
}

private fun HighlightInfo.toJson(): JsonObject {
    return JsonObject().apply {
        put("description", description)
        put("severity", severity.toString())
        //put("line", highlighter.range)
    }
}

fun String.withLineNumbers(): String {
    val lines = split("\n")
    return buildString {
        for (i in lines.indices) {
            append("${i + 1}|")
            append(lines[i])
            append("\n")
        }
    }
}
