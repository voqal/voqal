package dev.voqal.assistant.context

import com.intellij.openapi.vfs.VirtualFile
import dev.voqal.assistant.context.code.ICode
import dev.voqal.assistant.context.code.SelectedCode
import dev.voqal.assistant.context.code.ViewingCode
import io.vertx.core.json.JsonObject

data class DeveloperContext(
    val transcription: String,
    val openFiles: List<ICode> = emptyList(),
    val viewingFile: VirtualFile? = null,
    val viewingCode: ViewingCode? = null,
    val selectedCode: SelectedCode? = null,
    val activeBreakpoints: List<Int> = emptyList(),
    val textOnly: Boolean = false,
    val partialTranscription: Boolean = false,
    val chatMessage: Boolean = false,
    val relevantFiles: MutableList<ICode> = mutableListOf()
) : VoqalContext {

    fun toJson(): JsonObject {
        return JsonObject().apply {
            if (transcription.isNotEmpty()) {
                put("transcription", transcription + "\n")
            } else {
                put("transcription", transcription)
            }
            put("openFiles", openFiles.map { it.toJson() })
            viewingFile?.let { put("viewingFile", it.path) }
            viewingCode?.let { put("viewingCode", it.toJson()) }
            selectedCode?.let { put("selectedCode", it.toJson()) }
            put("activeBreakpoints", activeBreakpoints)
            put("textOnly", textOnly)
            put("partialTranscription", partialTranscription)
            put("chatMessage", chatMessage)
            put("relevantFiles", relevantFiles.map { it.toJson() })
        }
    }
}
