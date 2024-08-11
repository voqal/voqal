package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.mode.ToggleEditModeTool
import dev.voqal.services.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch

class CreateFileTool : VoqalTool() {

    companion object {
        const val NAME = "create_file"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val filename = args.getString("filename")
        if (filename != null) {
            log.info("Creating file: $filename")
            ApplicationManager.getApplication().invokeAndWait {
                val fName = filename.substringAfterLast("/")
                var fileType = FileTypeRegistry.getInstance().getFileTypeByFileName(fName)
                if (fileType is UnknownFileType) {
                    fileType = FileTypes.PLAIN_TEXT
                }
                val file = PsiFileFactory.getInstance(project).createFileFromText(fName, fileType, "")

                //add to parent directory
                val homeDir = project.service<VoqalSearchService>().getProjectRoot()
                val childPath = if (filename.contains("/")) {
                    filename.substringBeforeLast("/")
                } else "/"
                val parentDir = homeDir.findFileByRelativePath(childPath)
                if (parentDir != null) {
                    val addedFile = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                        PsiManager.getInstance(project).findDirectory(parentDir)?.add(file)
                    })

                    //open file in editor
                    val virtualFile = addedFile?.containingFile?.virtualFile
                    val fileEditors = virtualFile?.let {
                        FileEditorManager.getInstance(project).openFile(it, true)
                    }

                    //start edit mode
                    if (fileEditors?.get(0) is TextEditor) {
                        project.scope.launch {
                            project.service<VoqalToolService>().blindExecute(ToggleEditModeTool())
                        }
                    }
                } else {
                    project.scope.launch {
                        log.warn("Parent directory not found")
                        project.service<VoqalStatusService>().updateText("Parent directory not found")
                    }
                }
            }
            project.service<VoqalStatusService>().updateText("Created file: $filename")
        } else {
            log.warn("No filename provided")
            project.service<VoqalStatusService>().updateText("No filename provided")
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Create the specified file.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("filename", JsonObject().apply {
                    put("type", "string")
                    put("description", "The name/path of the file to create.")
                })
            })
            put("required", JsonArray().add("filename"))
        }.toString())
    )
}
