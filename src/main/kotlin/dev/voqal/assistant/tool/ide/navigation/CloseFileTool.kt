package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.focus.DetectedIntent
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class CloseFileTool : VoqalTool() {

    companion object {
        const val NAME = "close_file"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val filename = args.getString("filename")
        if (filename != null) {
            //close specific file
            val file = FileEditorManager.getInstance(project).openFiles.find { it.name == filename }
            if (file != null) {
                log.info("Closing file: $file")
                ApplicationManager.getApplication().invokeAndWait {
                    FileEditorManager.getInstance(project).closeFile(file)
                }
                project.service<VoqalStatusService>().updateText("Closed file: $filename")
                return
            } else {
                log.warn("File not found: $filename. Closing active file instead.")
            }
        }

        //close active file
        val fileEditor = directive.ide.editor //event.dataContext.getData("fileEditor")
        if (fileEditor == null) {
            log.debug("No file editor found")
            project.service<VoqalStatusService>().updateText("No file editor found")
            return
        }
        log.info("Closing file: $fileEditor")

        if (fileEditor is FileEditor) {
            ApplicationManager.getApplication().invokeAndWait {
                FileEditorManager.getInstance(project).closeFile(fileEditor.file)
            }
            project.service<VoqalStatusService>().updateText("Closed file: ${fileEditor.file.name}")
        } else {
            val virtualFile = fileEditor.virtualFile
            if (virtualFile == null) {
                log.debug("No virtual file found")
                project.service<VoqalStatusService>().updateText("No virtual file found")
                return
            }

            ApplicationManager.getApplication().invokeAndWait {
                FileEditorManager.getInstance(project).closeFile(virtualFile)
            }
            project.service<VoqalStatusService>().updateText("Closed file: ${virtualFile.name}")
        }
    }

    override suspend fun getTranscriptIntent(project: Project, transcript: SpokenTranscript): DetectedIntent? {
        return attemptIntentExtract(transcript.cleanTranscript)?.let { (intent, args) ->
            DetectedIntent(intent, args, transcript, this)
        }
    }

    fun attemptIntentExtract(rawString: String): Pair<String, Map<String, String>>? {
        return if (rawString == "close file") {
            Pair(NAME, mapOf())
        } else {
            null
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Close the specified file.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("filename", JsonObject().apply {
                    put("type", "string")
                    put("description", "The name/path of the file to close")
                })
            })
            put("required", JsonArray().add("filename"))
        }.toString())
    )
}
