package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.AnswerQuestionTool
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.VoqalSearchService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait

class ViewSourceTool : VoqalTool() {

    companion object {
        const val NAME = "view_source"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val name = args.getString("name") ?: args.getString("directive")
        log.info("Adding file to context: $name")
        doViewSource(directive, name)
    }

    private suspend fun doViewSource(directive: VoqalDirective, filename: String) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val promise = Promise.promise<Void>()
        val file = project.service<VoqalSearchService>().findFile(filename)
        if (file == null) {
            log.warn("Failed to find: $filename")
            val errorMessage = "Failed to find: $filename"
            project.service<VoqalDirectiveService>().handleResponse(errorMessage)
            project.service<VoqalStatusService>().updateText(errorMessage)
            promise.complete()
        } else {
            val document = ReadAction.compute(ThrowableComputable {
                FileDocumentManager.getInstance().getDocument(file)
            })
            if (document == null) {
                log.warn("Failed to get document for: $filename")
                val errorMessage = "Failed to get document for: $filename"
                project.service<VoqalDirectiveService>().handleResponse(errorMessage)
                project.service<VoqalStatusService>().updateText(errorMessage)
                promise.complete()
            } else {
                val languageOfFile = (FileTypeManager.getInstance().getFileTypeByFile(file)
                        as? LanguageFileType)?.language
                val documentText = ReadAction.compute(ThrowableComputable { document.text })
                directive.developer.relevantFiles.add(
                    ViewingCode(
                        code = documentText,
                        language = languageOfFile?.id?.lowercase() ?: "",
                        filename = filename
                    )
                )
                project.service<VoqalStatusService>().updateText("Viewed file: $filename")
                promise.complete()
            }
        }
        promise.future().coAwait()
    }

    override fun canShortcut(project: Project, call: FunctionCall): Boolean {
        val findFile: String?
        try {
            findFile = JsonObject(call.arguments).getString("directive")!!
        } catch (_: Exception) {
            return false
        }

        val exactMatches = project.service<VoqalSearchService>().findExactMatches(findFile)
        return exactMatches.size == 1
    }

    override fun isVisible(directive: VoqalDirective) = directive.assistant.promptSettings?.decomposeDirectives == true
    override fun supportsDirectiveMode() = true

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            if (directive.assistant.directiveMode) {
                append("Adds the contents of the specified file to the current context to assist LLM in answering questions. ")
                append("Order matters so make sure to call this tool before " + AnswerQuestionTool.NAME + ".")
            } else {
                append("Adds the contents of the specified file to the current context to assist LLM in answering questions.")
            }
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                if (directive.assistant.directiveMode) {
                    put("directive", JsonObject().apply {
                        put("type", "string")
                        put("description", "The directive to pass to the tool")
                    })
                } else {
                    put("name", JsonObject().apply {
                        put("type", "string")
                        put(
                            "description",
                            "The name of the file. Be as specific as you can. Do not make up file extensions."
                        )
                    })
                }
            })
            if (directive.assistant.directiveMode) {
                put("required", JsonArray().add("directive"))
            } else {
                put("required", JsonArray().add("name"))
            }
        }.toString())
    )
}
