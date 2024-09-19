package dev.voqal.assistant.context.code

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import dev.voqal.assistant.context.VoqalContext
import io.vertx.core.json.JsonObject

data class ViewingCode(
    override val code: String? = null,
    override val language: String = "",
    override val filename: String? = null,
    val problems: List<HighlightInfo> = emptyList(),
    val includedLines: List<IntRange> = emptyList()
) : VoqalContext, ICode {

    constructor(json: JsonObject) : this(
        code = json.getString("code"),
        language = json.getString("language"),
        filename = json.getString("filename")
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("code", code?.withOmitted())
            put("codeWithLineNumbers", code?.withLineNumbers())
            put("language", language)
            put("filename", filename)

            if (problems.isNotEmpty()) {
                put("problems", problems.map { it.toJson() })
            }
        }
    }

    private fun String.withOmitted(): String {
        val lines = split("\n")
        val endsWithNewLine = lines.last() == "\n"
        return buildString {
            for (i in lines.indices) {
                if (isOmittedLine(i)) {
                    val indent = lines[i].takeWhile { it.isWhitespace() }
                    append("$indent...omitted...")
                } else {
                    append(lines[i])
                }
                if (i < lines.size - 1 || endsWithNewLine) {
                    append("\n")
                }
            }
        }
    }

    private fun String.withLineNumbers(): String {
        val lines = split("\n")
        return buildString {
            for (i in lines.indices) {
                val lineNum = i + 1
                append("$lineNum|")
                if (isOmittedLine(i)) {
                    val indent = lines[i].takeWhile { it.isWhitespace() }
                    append("$indent...omitted...")
                } else {
                    append(lines[i])
                }
                append("\n")
            }
        }
    }

    private fun isOmittedLine(lineNum: Int): Boolean {
        if (includedLines.isEmpty()) return false
        return includedLines.none { it.contains(lineNum) }
    }
}
