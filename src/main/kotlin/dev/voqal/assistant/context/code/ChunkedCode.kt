package dev.voqal.assistant.context.code

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import dev.voqal.assistant.context.VoqalContext
import io.vertx.core.json.JsonObject

data class ChunkedCode(
    override val code: String? = null,
    override val language: String = "",
    override val filename: String? = null,
    val problems: List<HighlightInfo> = emptyList(),
    val startLine: Int = 0
) : VoqalContext, ICode {

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("code", code)
            put("codeWithLineNumbers", code?.withLineNumbers())
            put("language", language)
            put("filename", filename)

            if (problems.isNotEmpty()) {
                put("problems", problems.map { it.toJson() })
            }
        }
    }

    private fun String.withLineNumbers(): String {
        val lines = split("\n")
        return buildString {
            for (i in lines.indices) {
                val lineNum = i + 1 + startLine
                append("$lineNum|")
                append(lines[i])
                append("\n")
            }
        }
    }
}
