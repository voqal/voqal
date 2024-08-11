package dev.voqal.assistant.tool.code

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.util.IncorrectOperationException
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.assistant.tool.system.mode.ToggleEditModeTool
import dev.voqal.services.*
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import java.util.*

class CreateClassTool : VoqalTool() {

    companion object {
        const val NAME = "create_class"

        fun getFileExtensionForLanguage(language: Language): String {
            return getFileExtensionForLanguage(language.id)
        }

        fun getFileExtensionForLanguage(language: String): String {
            return when (language.lowercase()) {
                "java" -> "java"
                "python" -> "py"
                "javascript" -> "js"
                "kotlin" -> "kt"
                "groovy" -> "groovy"
                "rust" -> "rs"
                "go" -> "go"
                else -> throw IllegalArgumentException("Unsupported language: $language")
            }
        }

        fun getSupportedFileExtensions(): List<String> {
            return listOf("java", "py", "js", "kt", "groovy", "rs", "go") //todo: check .tsx
        }
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val language = args.getString("language")
        val lang = Language.findLanguageByID(getLanguageId(language))
        if (lang == null) {
            project.service<VoqalDirectiveService>().handleResponse(
                input = "Language $language is not available"
            )
            project.service<VoqalStatusService>().updateText("Tool $name")
        } else {
            val className = args.getString("class_name")
            val classType = args.getString("class_type")
            try {
                val fileEditors = createClass(project, lang, className, classType)
                project.service<VoqalStatusService>().updateText("Tool $name")

                //start edit mode
                if (fileEditors.first() is TextEditor) {
                    project.service<VoqalToolService>().blindExecute(ToggleEditModeTool())
                }
            } catch (ex: IncorrectOperationException) {
                project.service<VoqalDirectiveService>().handleResponse(
                    ex.message ?: "Failed to create class"
                )
            }
        }
    }

    private fun getLanguageId(language: String): String {
        return if (language.lowercase() == "java") {
            "JAVA"
        } else if (language.lowercase() == "kotlin") {
            "kotlin"
        } else if (language.lowercase() == "javascript") {
            "JavaScript"
        } else if (language.lowercase() == "python") {
            "Python"
        } else if (language.lowercase() == "groovy") {
            "Groovy"
        } else if (language.lowercase() == "go") {
            "go"
        } else {
            "JAVA" //todo: default to best guess
        }
    }

    private suspend fun createClass(
        project: Project,
        lang: Language,
        className: String,
        classType: String?
    ): List<FileEditor> {
        val fileName = className + "." + getFileExtensionForLanguage(lang)
        val newClassCode = if (lang.id == "go") {
            buildString {
                append("package main\n\n") //todo: dynamic package
                append("type ").append(className).append(" struct {\n")
                append("}\n")
            }
        } else if (lang.id == "Python") {
            buildString {
                append("class ").append(className).append(":").append("\n")
                append("\t").append("pass").append("\n")
            }
        } else {
            buildString {
                if (lang.id == "JAVA") {
                    append("public ")
                }
                append("${classType ?: "class"} ").append(className).append(" {\n")
                append("}")
            }
        }
        val scratchFile = ReadAction.compute(ThrowableComputable {
            PsiFileFactory.getInstance(project).createFileFromText(fileName, lang, newClassCode)
        }) ?: throw IllegalStateException("Failed to create scratch file")

        val sourceRoots = project.service<VoqalSearchService>().getSourceRoots()
        if (sourceRoots.size == 1) {
            val sourceRoot = sourceRoots.first()
            val psiDir = ReadAction.compute(ThrowableComputable {
                PsiManager.getInstance(project).findDirectory(sourceRoot)
            })
            if (psiDir == null) {
                TODO("todo")
            } else {
                val newClass = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                    psiDir.add(scratchFile)
                })
                val promise = Promise.promise<List<FileEditor>>()
                project.invokeLater {
                    promise.complete(
                        FileEditorManager.getInstance(project)
                            .openFile(newClass.containingFile.virtualFile)
                    )
                }
                return promise.future().coAwait()
            }
        } else if (sourceRoots.isEmpty()) {
            val modules = ModuleManager.getInstance(project).modules
            if (modules.size == 1) {
                val contentRoots = ModuleRootManager.getInstance(modules.first()).contentRoots
                if (contentRoots.size == 1) {
                    val sourceRoot = contentRoots.first().canonicalFile
                    val psiDir = sourceRoot?.let {
                        ReadAction.compute(ThrowableComputable {
                            PsiManager.getInstance(project).findDirectory(sourceRoot)
                        })
                    }
                    if (psiDir == null) {
                        TODO()
                    } else {
                        val newClass = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                            psiDir.add(scratchFile)
                        })
                        val promise = Promise.promise<List<FileEditor>>()
                        project.invokeLater {
                            promise.complete(
                                FileEditorManager.getInstance(project)
                                    .openFile(newClass.containingFile.virtualFile)
                            )
                        }
                        return promise.future().coAwait()
                    }
                } else {
                    TODO()
                }
            } else {
                TODO()
            }
        } else {
            //default to main source root (if exists)
            val mainSourceRoot = sourceRoots.firstOrNull {
                it.path.contains("src/main/" + lang.id.lowercase())
            }
            if (mainSourceRoot != null) {
                val psiDir = ReadAction.compute(ThrowableComputable {
                    PsiManager.getInstance(project).findDirectory(mainSourceRoot)
                })
                if (psiDir == null) {
                    TODO()
                } else {
                    val newClass = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                        psiDir.add(scratchFile)
                    })
                    val promise = Promise.promise<List<FileEditor>>()
                    project.invokeLater {
                        promise.complete(
                            FileEditorManager.getInstance(project)
                                .openFile(newClass.containingFile.virtualFile)
                        )
                    }
                    return promise.future().coAwait()
                }
            } else {
                println(sourceRoots)
                println(scratchFile)
                TODO("todo")
            }
        }
    }

    private fun createDirectory(parent: PsiDirectory, name: String?): PsiDirectory {
        var result: PsiDirectory? = null
        for (dir in parent.subdirectories) {
            if (dir.name.equals(name, ignoreCase = true)) {
                result = dir
                break
            }
        }
        if (null == result) {
            result = parent.createSubdirectory(name!!)
        }
        return result
    }

    private fun createPackage(sourceDir: PsiDirectory, qualifiedPackage: String?): PsiDirectory {
        var parent = sourceDir
        val token = StringTokenizer(qualifiedPackage, ".")
        while (token.hasMoreTokens()) {
            val dirName: String = token.nextToken()
            parent = createDirectory(parent, dirName)
        }
        return parent
    }

    override fun asTool(directive: VoqalDirective): Tool {
        val fullAppName = ApplicationInfo.getInstance().fullApplicationName
        var defaultLanguage = when {
            fullAppName.startsWith("GoLand") -> "go"
            fullAppName.startsWith("PyCharm") -> "Python"
            fullAppName.startsWith("WebStorm") -> "JavaScript"
            fullAppName.startsWith("RustRover") -> "Rust"
            else -> "JAVA"
        }

        //check folder structure for language
        val searchService = directive.ide.project.service<VoqalSearchService>()
        val sourceRoots = searchService.getSourceRoots()
        if (sourceRoots.isNotEmpty()) {
            val mainSourceRoot = sourceRoots.first()
            val lang = searchService.getLanguageForSourceRoot(mainSourceRoot)
            if (lang != null) {
                defaultLanguage = lang.id
            }
        }

        return Tool.function(
            name = NAME,
            description = buildString {
                append("Use this tool when the developer requests to create a new class. ")
                append("The new class should be empty except for necessary structure for classes of the given language. ")
            },
            parameters = Parameters.fromJsonString(JsonObject().apply {
                put("type", "object")
                put("properties", JsonObject().apply {
                    put("language", JsonObject().apply {
                        put("type", "string")
                        put("description", "The language type of the class (Java, Python, etc).")
                        put("default", defaultLanguage)
                    })
                    put("class_type", JsonObject().apply {
                        put("type", "string")
                        put("description", "The type of class to create.")
                        put(
                            "enum",
                            JsonArray().add("class").add("interface").add("enum").add("object").add("data class")
                        )
                        put("default", "class")
                    })
                    put("class_name", JsonObject().apply {
                        put("type", "string")
                        put("description", "The name of the class being created.")
                    })
                    put("path", JsonObject().apply {
                        put("type", "string")
                        put("description", "Path or package to place class (e.g. com.example, src/utils/, etc.)")
                    })
                })
                put("required", JsonArray().add("language").add("class_name"))
            }.toString())
        )
    }
}
