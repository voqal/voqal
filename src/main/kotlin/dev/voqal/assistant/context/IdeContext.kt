package dev.voqal.assistant.context

import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import io.vertx.core.json.JsonObject

data class IdeContext(
    val project: Project,
    val editor: Editor? = null,
    val carets: Map<Int, Caret> = emptyMap(),
    val projectFileTree: String? = null //todo: save as object and serialize as late as possible
) : VoqalContext {

    fun toJson(): JsonObject {
        return JsonObject().apply {
            put("project", project.name) //deprecated
            put("projectName", project.name)
            if (!projectFileTree.isNullOrBlank()) {
                put("projectFileTree", projectFileTree)
            }
        }
    }
}
