package dev.voqal.assistant.processing

import com.intellij.openapi.util.TextRange
import io.vertx.core.json.JsonObject
import java.util.regex.Pattern

object DocumentEditor {

    fun checkForVuiInteraction(type: String, responseCode: String): Boolean {
        try {
            val json = JsonObject(responseCode)
            if (json.getBoolean(type, false)) {
                return true
            }
        } catch (_: Exception) {
        }
        try {
            val extractedCode = extractCodeBlock(responseCode)
            val json = JsonObject(extractedCode)
            if (json.getBoolean(type, false)) {
                return true
            }
        } catch (_: Exception) {
        }

        //try stripping leading and trailing backticks
        if (responseCode.startsWith("`") && responseCode.endsWith("`")) {
            try {
                val trimmed = responseCode.trim('`')
                val json = JsonObject(trimmed)
                if (json.getBoolean(type, false)) {
                    return true
                }
            } catch (_: Exception) {
            }
        }
        return false
    }

    fun findText(needle: String, haystack: String, startIndex: Int = 0, lookForward: Boolean = true): TextRange? {
        if (lookForward) {
            val index = haystack.indexOf(needle, startIndex)
            if (index != -1) {
                return TextRange(index, index + needle.length)
            }

            //try ignoring all spacing differences
            val needlePattern = needle
                .split("\\s+".toRegex())
                .joinToString("\\s+") { Regex.escape(it) }
            val pattern = Pattern.compile(needlePattern)
            val matcher = pattern.matcher(haystack.substring(startIndex))
            if (matcher.find()) {
                //this is a bit greedy so make sure the start with the same amount of newlines
                val textRange = TextRange(matcher.start() + startIndex, matcher.end() + startIndex)
                val text = haystack.substring(textRange.startOffset, textRange.endOffset)
                val startNewlines = text.takeWhile { it == '\n' }.length
                val needleNewlines = needle.takeWhile { it == '\n' }.length
                //remove leading newlines if not the same
                if (startNewlines != needleNewlines) {
                    val newStart = text.dropWhile { it == '\n' }
                    return TextRange(textRange.startOffset + (text.length - newStart.length), textRange.endOffset)
                }
                return textRange
            }
        } else {
            val reverseHaystack = haystack.reversed()
            val reverseNeedle = needle.reversed()
            val adjustedStartIndex = haystack.length - startIndex - 1

            val index = reverseHaystack.indexOf(reverseNeedle, adjustedStartIndex)
            if (index != -1) {
                return TextRange(haystack.length - index - reverseNeedle.length, haystack.length - index)
            }

            //try ignoring all spacing differences
            val needlePattern = reverseNeedle
                .split("\\s+".toRegex())
                .joinToString("\\s+") { Regex.escape(it) }
            val pattern = Pattern.compile(needlePattern)
            val matcher = pattern.matcher(reverseHaystack.substring(adjustedStartIndex))
            if (matcher.find()) {
                //this is a bit greedy so make sure the start with the same amount of newlines
                val reverseStart = matcher.start() + adjustedStartIndex
                val reverseEnd = matcher.end() + adjustedStartIndex
                val startOffset = haystack.length - reverseEnd
                val endOffset = haystack.length - reverseStart
                val textRange = TextRange(startOffset, endOffset)
                val text = haystack.substring(textRange.startOffset, textRange.endOffset)
                val startNewlines = text.takeWhile { it == '\n' }.length
                val needleNewlines = needle.takeWhile { it == '\n' }.length
                //remove leading newlines if not the same
                if (startNewlines != needleNewlines) {
                    val newStart = text.dropWhile { it == '\n' }
                    return TextRange(textRange.startOffset + (text.length - newStart.length), textRange.endOffset)
                }
                return textRange
            }
        }

        return null
    }

    fun isEmptyCodeBlock(text: String): Boolean {
        //general, just empty curly braces
        val check1 = Regex("""(.+)\s*\{\s*}""")
        val match1 = check1.matches(text)
        if (match1) return true

        //python, just pass inside
        val check2 = Regex("(.+)\\n\\s+pass$")
        val match2 = check2.matches(text)
        if (match2) return true

        return false
    }

    //todo: took from EditTextTool, may be necessary
    //val regex = Regex("```(?:[a-zA-Z0-9]+)?\\n((?:(?!```)[\\s\\S])+)\\n```")
    fun extractCodeBlock(text: String): String {
        val doubleBacktickErrCodeBlock = doStrictExtractCodeBlockDoubleBacktickError(text)
        if (doubleBacktickErrCodeBlock != null) {
            return doubleBacktickErrCodeBlock
        }
        val errCodeBlock = doStrictExtractCodeBlockNewLineError(text)
        if (errCodeBlock != null) {
            return errCodeBlock
        }
        val codeBlock = doStrictExtractCodeBlock(text)
        if (codeBlock != null) {
            return codeBlock
        }
        val codeSnippet = doStrictExtractCodeSnippet(text)
        if (codeSnippet != null) {
            return codeSnippet
        }

        var theText = text.replace("\r\n", "\n")
        if (theText.contains("\\n")) { //todo: more robust check if code is escaped
            theText = theText.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
        }
        var trimmedText = theText.trim()
        if (trimmedText.startsWith("```") && !trimmedText.endsWith("```")) {
            trimmedText += "\n```"
        }

        val finalText = when {
            trimmedText.startsWith("```") && trimmedText.endsWith("```") -> {
                val startIndex = "```".length
                val endIndex = "```".length
                val codeBlock = trimmedText.substring(startIndex, trimmedText.length - endIndex)
                val language = codeBlock.takeWhile { it != '\n' }
                println("Language: $language")
                val codeLines = codeBlock.split("\n").drop(1)
                val cleanedLines = codeLines.dropWhile { it.isBlank() }.filter { it.isNotBlank() }
                cleanedLines.joinToString("\n")
            }

            trimmedText.startsWith("`") && trimmedText.endsWith("`") -> {
                val count = trimmedText.takeWhile { it == '`' }.length
                trimmedText.substring(count, trimmedText.length - count).trimStart()
            }

            else -> theText
        }
        return finalText
    }

    /**
     * Extracts code block with erroneous double backticks lines.
     * Example:
     * ```
     * ```java
     * <code>
     * ```
     * ```
     */
    private fun doStrictExtractCodeBlockDoubleBacktickError(code: String): String? {
        val pattern = Pattern.compile("```\\n```(?:[\\w ]+)?[\\n\\r\\s]([\\s\\S]*?)\\s*?```\\n```")
        val matcher = pattern.matcher(code)
        return if (matcher.find()) {
            val text = matcher.group(1)
            val textLines = text.split("\n")
            if (textLines.getOrNull(0) == "") {
                textLines.drop(1).joinToString("\n")
            } else {
                text
            }
        } else {
            null
        }
    }

    /**
     * Extracts code block with erroneous language name on new line.
     * Example:
     * ```
     * java
     * <code>
     * ```
     */
    private fun doStrictExtractCodeBlockNewLineError(code: String): String? {
        val pattern = Pattern.compile("```\\n(?:[\\w]+)\\n([\\s\\S]*?)\\s*?```")
        val matcher = pattern.matcher(code)
        return if (matcher.find()) {
            val text = matcher.group(1)
            val textLines = text.split("\n")
            if (textLines.getOrNull(0) == "") {
                textLines.drop(1).joinToString("\n")
            } else {
                text
            }
        } else {
            null
        }
    }

    /**
     * Extracts code block with optional language name.
     * Example:
     * ```(optional)
     * <code>
     * ```
     */
    fun doStrictExtractCodeBlock(code: String): String? {
        val pattern = Pattern.compile("```(?:[\\w ]+)?[\\n\\r\\s]([\\s\\S]*?)\\s*?```")
        val matcher = pattern.matcher(code)
        return if (matcher.find()) {
            val text = matcher.group(1)
            val textLines = text.split("\n")
            if (textLines.getOrNull(0) == "") {
                textLines.drop(1).joinToString("\n")
            } else {
                text
            }
        } else {
            null
        }
    }

    private fun doStrictExtractCodeSnippet(code: String): String? {
        val pattern = Pattern.compile("^```([\\s\\S]*?)```\$")
        val matcher = pattern.matcher(code)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }
}
