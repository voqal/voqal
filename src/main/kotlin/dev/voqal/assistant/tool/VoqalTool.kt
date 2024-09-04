package dev.voqal.assistant.tool

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import io.vertx.core.json.JsonObject

/**
 * LLM-executable functionality.
 */
abstract class VoqalTool {

    abstract val name: String
    abstract suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective)
    abstract fun asTool(directive: VoqalDirective): Tool
    open fun isVisible(directive: VoqalDirective): Boolean = true
    open fun supportsDirectiveMode(): Boolean = false
    open fun canShortcut(project: Project, call: FunctionCall): Boolean = false
    open suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? = null
}
