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

    fun extractCodeBlock(text: String): String {
        var escapedText = text.replace("\r\n", "\n")
        if (escapedText.contains("\\n")) { //todo: more robust check if code is escaped
            escapedText = escapedText.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
        }

        val codeBlock = doStrictExtractCodeBlock(escapedText)
        if (codeBlock != null) {
            return codeBlock
        }
        val codeSnippet = doStrictExtractCodeSnippet(escapedText)
        if (codeSnippet != null) {
            return codeSnippet
        }
        return escapedText
    }

    /**
     * ````
     *```
     *```(java)
     *<code>
     *```
     * ````
     */
    private val extraBacktickHeaderBlock = Pattern.compile("```\\n```(?:[\\w ]+)?[\\n\\r\\s]([\\s\\S]*?)\\s*?```")

    /**
     * ````
     *```
     *```(java)
     *<code>
     *```
     *```
     * ````
     */
    private val doubleBacktickBlock = Pattern.compile("```\\n```(?:[\\w ]+)?[\\n\\r\\s]([\\s\\S]*?)\\s*?```\\n```")

    /**
     * ````
     *```
     *java
     *<code>
     *```
     *````
     */
    private val langOnNewLineBlock = Pattern.compile("```\\n(?:[\\w]+)\\n([\\s\\S]*?)\\s*?```")

    /**
     * ````
     *```(java)
     *<code>
     *```
     *````
     */
    private val defaultCodeBlock = Pattern.compile("```(?:[\\w ]+)?[\\n\\r\\s]([\\s\\S]*?)\\s*?```")

    private val codeBlockPatterns = listOf(
        extraBacktickHeaderBlock,
        doubleBacktickBlock,
        langOnNewLineBlock,
        defaultCodeBlock
    )

    private fun doStrictExtractCodeBlock(code: String): String? {
        for (pattern in codeBlockPatterns) {
            val matcher = pattern.matcher(code)
            if (matcher.find()) {
                val text = matcher.group(1)
                val textLines = text.split("\n")
                return if (textLines.getOrNull(0) == "") {
                    textLines.drop(1).joinToString("\n")
                } else {
                    text
                }
            }
        }
        return null
    }

    /**
     * ````
     *```<code>```
     * ````
     */
    private val tripleTickSnippet = Pattern.compile("^```([\\s\\S]*?)```\$")

    /**
     * ```
     *`<code>`
     * ```
     */
    private val singleTickSnippet = Pattern.compile("^`([\\s\\S]*?)`$")

    private val codeSnippetPatterns = listOf(
        tripleTickSnippet,
        singleTickSnippet
    )

    private fun doStrictExtractCodeSnippet(code: String): String? {
        for (pattern in codeSnippetPatterns) {
            val matcher = pattern.matcher(code)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null
    }
}
