package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.services.*
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.launch

class OpenFileTool : NavigateFileTool(NavigateOperation.OPEN) {

    companion object {
        const val NAME = "open_file"
    }

    override val name = NAME

    override suspend fun doFileOperation(directive: VoqalDirective, filename: String?) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val promise = Promise.promise<Void>()
        val file = filename?.let { project.service<VoqalSearchService>().findFile(it) }
        if (file == null) {
            log.warn("Failed to find: $filename")
            val errorMessage = "Failed to find: $filename"
            project.service<VoqalDirectiveService>().handleResponse(errorMessage)
            project.service<VoqalStatusService>().updateText(errorMessage)
            promise.complete()
        } else {
            project.invokeLater {
                log.info("Opening: $file")
                FileEditorManager.getInstance(project).openFile(file, true)
                project.scope.launch {
                    project.service<VoqalStatusService>().updateText("Opened file: $filename")
                    promise.complete()
                }
            }
        }
        promise.future().coAwait()
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            append("Opens a particular file. Do not use this tool unless specifically requested to open something. ")
            append("Do not include file extension unless provided. Do not make up filenames. ")
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("name", JsonObject().apply {
                    put("type", "string")
                    put(
                        "description",
                        "The name of the file. Be as specific as you can. Do not make up file extensions."
                    )
                })
            })
            put("required", JsonArray().add("name"))
        }.toString())
    )
}
