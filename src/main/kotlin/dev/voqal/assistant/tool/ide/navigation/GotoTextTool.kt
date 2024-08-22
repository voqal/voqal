package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.processing.TextExtractor
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.math.max

class GotoTextTool : VoqalTool() {

    companion object {
        const val NAME = "goto_text"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val text = args.getString("text").toString()
        val editor = directive.ide.editor
        if (editor != null) {
            log.info("Going to text: $text")

            //get line number of text
            val documentText = editor.document.text
            val memoryService = project.service<VoqalMemoryService>()
            var startOffset = 0
            var forward = true
            if (args.getString("direction") != null) {
                startOffset = memoryService.getLongTermUserData("goto_text_last_offset")
                    ?.toString()?.toInt()?.let { it + 1 } ?: 0
                forward = args.getString("direction") == "next"
            }
            val textRange = TextExtractor.findText(text, documentText, startOffset, forward)
            if (textRange != null) {
                val offset = textRange.startOffset
                memoryService.putLongTermUserData("goto_text_last_offset", offset)

                val lineNumber = editor.document.getLineNumber(offset) + 1
                log.info("Going to text on line: $lineNumber")
                ApplicationManager.getApplication().invokeAndWait {
                    val logicalPosition = LogicalPosition(max(0, lineNumber - 1), 0) //intellij is 0-based
                    editor.caretModel.moveToLogicalPosition(logicalPosition)
                    editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
                }
                project.service<VoqalStatusService>().updateText("Goto text on line: $lineNumber")
            } else {
                log.warn("Unable to find text: $text")
                project.service<VoqalStatusService>().updateText("Unable to find text: $text")
            }
        } else {
            log.warn("No selected text editor")
            project.service<VoqalStatusService>().updateText("No selected text editor")
        }
    }

    //todo: shortcuts

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Go to the specified text.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("text", JsonObject().apply {
                    put("type", "string")
                    put("description", "The text to go to.")
                })
            })
            put("required", JsonArray().add("text"))
        }.toString())
    )
}
