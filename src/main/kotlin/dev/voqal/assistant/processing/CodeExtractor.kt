package dev.voqal.assistant.processing

import java.util.regex.Pattern

object CodeExtractor {

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
