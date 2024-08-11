package dev.voqal.assistant.context.code

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.Caret
import com.intellij.psi.PsiElement
import dev.voqal.assistant.context.VoqalContext
import io.vertx.core.json.JsonObject

data class ViewingCode(
    override val code: String? = null,
    val codeWithLineNumbers: String? = code?.withLineNumbers(),
    override val language: String = "",
    override val filename: String? = null,
    val psiAtCaret: PsiElement? = null,
    val caret: Caret? = null,
    val caretId: Int? = caret?.let { System.identityHashCode(it) },
    val caretOffset: Int? = caret?.offset,
    val caretLine: Int? = caret?.logicalPosition?.line,
    val caretColumn: Int? = caret?.logicalPosition?.column,
    val problems: List<HighlightInfo> = emptyList()
) : VoqalContext, ICode {

    constructor(json: JsonObject) : this(
        code = json.getString("code"),
        codeWithLineNumbers = json.getString("codeWithLineNumbers"),
        language = json.getString("language"),
        filename = json.getString("filename"),
        caretId = json.getInteger("caretId"),
        caretOffset = json.getInteger("caretOffset"),
        caretLine = json.getInteger("caretLine"),
        caretColumn = json.getInteger("caretColumn")
    )

    override fun toJson(): JsonObject {
        return JsonObject().apply {
            put("code", code)
            put("codeWithLineNumbers", codeWithLineNumbers)
            val codeWithCaret = code?.let { codeWithCaret(it, caretLine, caretColumn) }
            put("codeWithCaret", codeWithCaret)
            put("language", language)
            put("filename", filename)
            put("caretId", caretId)
            put("caretOffset", caretOffset)
            put("caretLine", caretLine)
            put("caretColumn", caretColumn)
            put("caretVisible", codeWithCaret != null && codeWithCaret.contains("↕"))

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

fun codeWithCaret(code: String, line: Int?, column: Int?): String {
    if (line == null || column == null) return code
    val lines = code.split("\n")
    if (line < 0 || line >= lines.size) return code
    val lineContent = lines[line]
    if (column < 0 || column > lineContent.length) return code
    return buildString {
        for (i in lines.indices) {
            if (i == line) {
                append(lineContent.substring(0, column))
                append("↕")
                append(lineContent.substring(column))
            } else {
                append(lines[i])
            }
            append("\n")
        }
    }
}