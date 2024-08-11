package dev.voqal.assistant.tool

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.services.VoqalToolService.Companion.fixIllegalDollarEscape
import io.vertx.core.json.JsonObject

abstract class VoqalTool {

    abstract val name: String
    abstract suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective)
    abstract fun asTool(directive: VoqalDirective): Tool
    open fun isVisible(directive: VoqalDirective): Boolean = true
    open fun supportsDirectiveMode(): Boolean = false
    open fun canShortcut(project: Project, call: FunctionCall): Boolean = false
    open suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? = null

    companion object {
        data class ToolCallSortCriteria(
            val line: Int,
            val column: Int
        ) : Comparable<ToolCallSortCriteria> {
            override fun compareTo(other: ToolCallSortCriteria): Int {
                if (line != other.line) {
                    return line.compareTo(other.line)
                }
                return column.compareTo(other.column)
            }
        }

        fun asSortedToolCalls(toolCalls: List<ToolCall>): List<ToolCall> {
            return toolCalls.sortedWith(compareByDescending { toolCall ->
                try {
                    val toolCallArgs = JsonObject(
                        fixIllegalDollarEscape((toolCall as ToolCall.Function).function.arguments)
                    )
                    val line = toolCallArgs.getInteger("line", 0)
                    val column = toolCallArgs.getInteger("column", 0)

                    ToolCallSortCriteria(line, column)
                } catch (e: Exception) {
                    ToolCallSortCriteria(0, 0)
                }
            })
        }
    }
}
