package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.execution.*
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiElement
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class RunProgramTool : VoqalTool() {

    companion object {
        const val NAME = "run_program"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val projectRunConfigName = args.getString("project_run_configuration")
        val fileRunConfigName = args.getString("file_run_configuration")
        val debugMode = args.getString("debug_mode", "false").toBooleanStrict()
        val projectRunConfig = getProjectRunConfig(project, projectRunConfigName)
        val fileRunConfig = directive.ide.editor?.let { getFileRunConfig(project, it.document, fileRunConfigName) }

        val runConfig = projectRunConfig ?: fileRunConfig
        if (runConfig == null) {
            log.warn("No run configuration found: $args")
            return
        }

        startRun(project, runConfig, debugMode)
        project.service<VoqalStatusService>().updateText("Executed run config: " + runConfig.name)
    }

    private fun getProjectRunConfig(project: Project, projectRunConfigName: String?): RunConfiguration? {
        return projectRunConfigName?.let {
            RunManager.getInstance(project).allConfigurationsList.find {
                //make alphanumeric_- only
                val cleanedRunConfigName = it.toString().replace(Regex("[^A-Za-z0-9_-]"), "_")
                cleanedRunConfigName == projectRunConfigName
            }
        }
    }

    private fun getFileRunConfig(project: Project, document: Document, fileRunConfigName: String?): RunConfiguration? {
        val fileRunConfigs = DaemonCodeAnalyzerImpl.getLineMarkers(document, project).filter {
            it::class.java.name.contains("RunLineMarkerInfo")
        }
        return fileRunConfigs.mapNotNull { getFileRunConfigExecution(project, it) }.firstOrNull {
            //make alphanumeric_- only
            val cleanedRunConfigName = it.name.replace(Regex("[^A-Za-z0-9_-]"), "_")
            cleanedRunConfigName == fileRunConfigName
        }?.configuration
    }

    private fun startRun(project: Project, runConfiguration: RunConfiguration, debugMode: Boolean) {
        val executor = if (debugMode) {
            DefaultDebugExecutor.getDebugExecutorInstance()
        } else {
            DefaultRunExecutor.getRunExecutorInstance()
        }
        val builder = ExecutionEnvironmentBuilder.create(project, executor, runConfiguration)
        val executionEnv = ReadAction.compute<ExecutionEnvironment, Exception> {
            builder.contentToReuse(null).dataContext(null).activeTarget().build()
        }

        project.invokeLater {
            ProgramRunnerUtil.executeConfiguration(executionEnv, false, true)
        }
    }

    override fun isVisible(directive: VoqalDirective): Boolean {
        val fileRunConfigs = directive.ide.editor?.let {
            getFileRunConfigs(directive.project, it)
        } ?: JsonArray()
        val projectRunConfigs = getProjectRunConfigs(directive.project)
        return !fileRunConfigs.isEmpty || !projectRunConfigs.isEmpty
    }

    override fun asTool(directive: VoqalDirective): Tool {
        val fileRunConfigs = directive.ide.editor?.let { getFileRunConfigs(directive.ide.project, it) } ?: JsonArray()
        val projectRunConfigs = getProjectRunConfigs(directive.ide.project)

        return Tool.function(
            name = NAME,
            description = buildString {
                append("Executes/runs a particular file, file run configuration, or project run configuration. ")
                append("Prefer file run configurations over project run configurations over file names. ")
                append("If you only have one run configuration, consider that the 'this' in 'run this'.")
            },
            parameters = Parameters.fromJsonString(JsonObject().apply {
                put("type", "object")
                put("properties", JsonObject().apply {
                    if (!fileRunConfigs.isEmpty) {
                        put("file_run_configuration", JsonObject().apply {
                            put("type", "string")
                            put("description", "The file run configuration to run.")
                            put("enum", fileRunConfigs)
                        })
                    }
                    if (!projectRunConfigs.isEmpty) {
                        put("project_run_configuration", JsonObject().apply {
                            put("type", "project_run_configuration")
                            put("description", "The project run configuration to run.")
                            put("enum", projectRunConfigs)
                        })
                    }
                    put("debug_mode", JsonObject().apply {
                        put("type", "boolean")
                        put("description", "Whether or not to run in debug mode, default is false")
                        put("default", false)
                    })
                })
            }.toString())
        )
    }

    private fun getFileRunConfigs(project: Project, editor: Editor): JsonArray {
        val fileRunConfigs = DaemonCodeAnalyzerImpl.getLineMarkers(editor.document, project).filter {
            it::class.java.name.contains("RunLineMarkerInfo")
        }

        val namedFileRunConfigs = JsonArray()
        fileRunConfigs.forEach {
            val runConfig = getFileRunConfigExecution(project, it)
            runConfig?.name?.let {
                //make alphanumeric_- only
                val cleanedRunConfigName = it.replace(Regex("[^A-Za-z0-9_-]"), "_")
                namedFileRunConfigs.add(cleanedRunConfigName)
            }
        }
        return namedFileRunConfigs
    }

    private fun getFileRunConfigExecution(project: Project, it: LineMarkerInfo<*>): RunnerAndConfigurationSettings? {
        return ReadAction.compute(ThrowableComputable {
            val location = PsiLocation.fromPsiElement<PsiElement>(it.element)
            val dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, project)
                .add( //todo: should these be included if they are null?
                    PlatformCoreDataKeys.MODULE,
                    it.element?.let { element -> ModuleUtilCore.findModuleForPsiElement(element) }
                )
                .add(Location.DATA_KEY, location)
                .build()

            ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN).configuration
        })
    }

    private fun getProjectRunConfigs(project: Project): JsonArray {
        val runManager = RunManager.getInstance(project)
        val runConfigs = runManager.allConfigurationsList

        val namedFileRunConfigs = JsonArray()
        runConfigs.map { it.toString() }.map {
            //make alphanumeric_- only
            val cleanedRunConfigName = it.replace(Regex("[^A-Za-z0-9_-]"), "_")
            namedFileRunConfigs.add(cleanedRunConfigName)
        }
        return namedFileRunConfigs
    }
}
