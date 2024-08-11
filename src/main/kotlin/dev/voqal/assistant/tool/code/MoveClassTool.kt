package dev.voqal.assistant.tool.code

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalSearchService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import org.joor.Reflect

class MoveClassTool : VoqalTool() {

    companion object {
        const val NAME = "move_class"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val className = args.getString("class_name")
        val newPackage = args.getString("new_package").lowercase()
        log.info("Moving class $className to package $newPackage")
        val searchService = project.service<VoqalSearchService>()
        val classFile = searchService.findFile(className)
        val promise = Promise.promise<Unit>()
        if (classFile != null) {
            log.debug("Found class file: $classFile")
            ApplicationManager.getApplication().runReadAction {
                val psiFile = PsiManager.getInstance(project).findFile(classFile)
                if (psiFile == null) {
                    log.warn("No PSI file found")
                    return@runReadAction
                }
                val sourceRoot = searchService.getMainSourceRoot(psiFile.language)
                if (sourceRoot == null) {
                    log.warn("No source root found")
                    return@runReadAction
                }

                val flattenedPackages = searchService.getFlattenedPackages()
                val bestGuess = flattenedPackages.firstOrNull { it.endsWith(newPackage) } ?: newPackage
                log.info("Moving to $bestGuess")

                val dir = searchService.getPackageByName(sourceRoot, bestGuess)
                if (dir != null) {
                    val clazz = mutableListOf<PsiElement>()
                    try {
                        val classes = Reflect.on(psiFile).call("getClasses").get<Array<PsiElement>>()
                        clazz.addAll(classes)
                    } catch (_: Exception) {
                    }

                    if (clazz.isNotEmpty()) {
                        project.invokeLater {
                            ApplicationManager.getApplication().runWriteAction {
                                var psiClass: PsiElement? = null
                                try {
                                    val firstClazz = clazz.first()
                                    val clazzType = Reflect.on(firstClazz).type()
                                    val isPsiClass = firstClazz::class.java.classLoader
                                        .loadClass("com.intellij.psi.PsiClass")
                                        .isAssignableFrom(clazzType)
                                    if (isPsiClass) {
                                        psiClass = firstClazz
                                    }
                                } catch (_: Exception) {
                                }

                                if (psiClass != null) {
                                    WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                                        Reflect.onClass("com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil")
                                            .call("doMoveClass", psiClass, dir)
                                    })
                                    promise.complete()
                                } else {
                                    log.warn("No class found")
                                    promise.complete()
                                }
                            }
                        }
                    } else {
                        log.warn("No class found")
                        promise.complete()
                    }
                } else {
                    log.warn("No directory found")
                    promise.complete()
                }
            }
            promise.future().coAwait()
        } else {
            log.warn("No class file found")
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = buildString {
            append("Use this tool when the developer requests to move a class to a new package. ")
        },
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("class_name", JsonObject().apply {
                    put("type", "string")
                    put("description", "The name of the class being moved. Be as specific as possible.")
                })
                put("new_package", JsonObject().apply {
                    put("type", "string")
                    put("description", "The new package name. Be as specific as possible.")
                })
            })
        }.toString())
    )
}
