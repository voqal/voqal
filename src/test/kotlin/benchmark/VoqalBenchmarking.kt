package benchmark

import benchmark.model.BenchmarkPromise
import benchmark.model.DirectiveResult
import benchmark.model.context.*
import benchmark.model.metadata.SupportLanguages
import benchmark.suites.edit.*
import benchmark.suites.edit.range.EditRangeSuite
import benchmark.suites.idle.AddBreakpointsSuite
import benchmark.suites.idle.GotoSuite
import benchmark.suites.idle.OpenFileSuite
import com.aallam.openai.api.audio.Transcription
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.ProperTextRange
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.utils.vfs.getDocument
import com.intellij.util.WaitFor
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.AssistantContext
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.template.ChunkTextExtension
import dev.voqal.config.VoqalConfig
import dev.voqal.config.settings.PromptSettings.EditFormat
import dev.voqal.services.*
import dev.voqal.status.VoqalStatus.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.launch
import org.joor.Reflect
import java.io.File
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMembers

class VoqalBenchmarking : JBTest() {

    private val editors = mutableListOf<DelegateEditor>()
    private val benchmarkVersion = System.getenv("VQL_BENCHMARK_VERSION") ?: "unknown"
    private var benchmarkSuite = ""
    private val benchmarkPromises = mutableListOf<BenchmarkPromise>()
    private var disposed = false
    private var error: Error? = null

    fun `test run idle mode suites`() {
        benchmarkSuite = "idle_mode"
        val config = TEST_CONFIG
        executeIdleMode(config)
        waitTillFinished()
    }

    fun `test run edit mode suites`() {
        benchmarkSuite = "edit_mode"
        val config = TEST_CONFIG
        executeEditMode(config)
        waitTillFinished()
    }

    private fun executeEditMode(config: VoqalConfig) {
        runBenchmarkSuite(AddFieldSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(RemoveFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(RenameFunctionParamSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(ModifyFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(RecursifyFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(ExtractFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(InlineFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(MissingImportsSuite(), benchmarkVersion, config, benchmarkPromises)

        runBenchmarkSuite(EditRangeSuite(), benchmarkVersion, config, benchmarkPromises)

        //todo: neither full text nor diff handle well yet
        //runBenchmarkSuite(MoveFunctionSuite(), benchmarkVersion, config, benchmarkPromises)
    }

    private fun executeIdleMode(config: VoqalConfig) {
        runBenchmarkSuite(AddBreakpointsSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(OpenFileSuite(), benchmarkVersion, config, benchmarkPromises)
        runBenchmarkSuite(GotoSuite(), benchmarkVersion, config, benchmarkPromises)
    }

    private fun waitTillFinished() {
        val fifoStack = LinkedList<BenchmarkPromise>()
        benchmarkPromises.forEach {
            fifoStack.add(it)
        }
        if (fifoStack.isEmpty()) {
            log.warn("Found no benchmarks to run")
            return
        }

        var benchPromise = fifoStack.pop()
        project.service<VoqalDirectiveService>().onDirectiveExecution { _, execution ->
            if (execution.endTime == null) return@onDirectiveExecution //wait till finished
            benchPromise.directiveId = execution.directive.directiveId
            benchPromise.response = execution.response
            benchPromise.errors = execution.errors

            benchPromise.stopFloorTime()
            project.invokeLater {
                benchPromise.promise.complete(benchPromise)

                if (fifoStack.isNotEmpty()) {
                    benchPromise = fifoStack.pop()
                    project.scope.launch {
                        run(benchPromise)
                    }
                } else {
                    project.invokeLater {
                        editors.forEach {
                            EditorFactory.getInstance().releaseEditor(it.delegate)
                        }
                        disposed = true
                    }
                }
            }
        }
        project.scope.launch {
            run(benchPromise)
        }

        object : WaitFor(1_200_000) { //20 minutes
            override fun condition(): Boolean {
                try {
                    PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
                } catch (ex: Error) {
                    error = ex
                    return true
                }
                return disposed
            }
        }.apply {
            if (error == null) {
                assertCompleted()
            }
        }
        log.info("All benchmarks completed")
    }

    private fun runBenchmarkSuite(
        benchmark: Any,
        benchmarkVersion: String,
        config: VoqalConfig,
        benchmarkPromises: MutableList<BenchmarkPromise>
    ) {
        benchmark::class.declaredMembers.filter {
            it.visibility == KVisibility.PUBLIC
        }.forEach {
            //skip bench if unsupported language
            val supportedLanguages = it.annotations.find { it is SupportLanguages } as? SupportLanguages
            if (supportedLanguages != null) {
                val currentLang = System.getenv("VQL_LANG").lowercase()
                val supported = supportedLanguages.languages.map { it.lowercase() }.contains(currentLang)
                if (!supported) {
                    return@forEach
                }
            }

            val benchPromise = BenchmarkPromise(
                project,
                benchmarkVersion,
                benchmark,
                it,
                config.languageModelsSettings.models.first().modelName,
                benchmark::class.simpleName!!
            )
            benchmarkPromises.add(benchPromise)
        }
    }

    private fun run(benchPromise: BenchmarkPromise) {
        System.setProperty("voqal.benchmark.name", benchPromise.testName)
        val contexts = benchPromise.callable.call(benchPromise.instance, benchPromise) as List<*>
        if (contexts.isEmpty()) {
            log.warn("Skipping benchmark: ${benchPromise.testName}")
            return
        }
        val transcription = Transcription(benchPromise.testName)
        val directiveService = project.service<VoqalDirectiveService>()

        project.invokeLater {
            contexts.mapNotNull { it as? ProjectFileContext }.forEach {
                myFixture.addFileToProject(it.virtualFile.name, it.virtualFile.getDocument().text)
            }
            contexts.mapNotNull { it as? OpenFileContext }.forEach {
                myFixture.openFileInEditor(it.virtualFile)
            }

            val virtualFile = (contexts.find { it is VirtualFileContext } as? VirtualFileContext)?.virtualFile
            val visibleRange = contexts.filterIsInstance<VisibleRangeContext>().firstOrNull()?.visibleRange
            val editor = virtualFile?.let {
                object : DelegateEditor(EditorFactory.getInstance().createEditor(it.getDocument(), project)) {
                    override fun calculateVisibleRange(): ProperTextRange {
                        return visibleRange ?: ProperTextRange(0, document.textLength)
                    }
                }
            }
            editor?.let {
                editors.add(it)

                if (visibleRange != null) {
                    ChunkTextExtension.setEditRangeHighlighter(project, it, visibleRange)
                }
            }

            benchPromise.startFloorTime()
            project.scope.launch {
                try {
                    project.service<VoqalStatusService>().update(EDITING)
                    val contextService = project.service<VoqalContextService>()
                    val directive = VoqalDirective(
                        assistant = AssistantContext(
                            memorySlice = getMemorySystem().getMemorySlice(),
                            availableActions = project.service<VoqalToolService>().getAvailableTools().values,
                            promptSettings = contexts.filterIsInstance<PromptSettingsContext>().first().settings.copy(
                                codeSmellCorrection = false, //todo: set via env
                                editFormat = EditFormat.valueOf(System.getenv("VQL_EDIT_FORMAT") ?: "FULL_TEXT")
                            ),
                            languageModelSettings = TEST_CONFIG.languageModelsSettings.models.first(),
                            includeToolsInMarkdown = System.getenv("VQL_MARKDOWN_TOOLS") != "false"
                        ),
                        ide = IdeContext(
                            project = project,
                            editor = editor,
                            projectFileTree = contextService.getProjectStructureAsMarkdownTree()
                        ),
                        developer = DeveloperContext(
                            transcription = transcription.text,
                            viewingFile = virtualFile,
                            viewingCode = contexts.find { it is ViewingCode } as? ViewingCode,
                            textOnly = true,
                            openFiles = contextService.getOpenFiles(editor)
                        )
                    )
                    directiveService.executeDirective(directive)
                    project.service<VoqalStatusService>().update(IDLE)
                } catch (e: Error) {
                    e.printStackTrace()
                    project.service<VoqalStatusService>().update(ERROR)
                }
            }
        }
    }

    override fun tearDown() {
        val err = error
        if (err != null) {
            super.tearDown()
            throw err
        }

        val finalResults = mutableListOf<DirectiveResult>()
        benchmarkPromises.forEach {
            finalResults.add(it.future().toCompletionStage().toCompletableFuture().get())
        }
        val modelName = finalResults.firstOrNull()?.modelName
        val directiveResults = JsonArray(finalResults.map { it.toJson() })
        if (benchmarkVersion != "unknown") {
            val benchmarkResult = JsonObject()
                .put("modelName", modelName)
                .put("benchmarkVersion", benchmarkVersion)
                .put("benchmarkSuite", benchmarkSuite)
                .put("results", directiveResults)

            val benchOut = File("benchmark").apply { mkdirs() }
            val currentLang = System.getenv("VQL_LANG").lowercase()
            File(benchOut, "$benchmarkSuite-$currentLang.json").writeText(benchmarkResult.toString())
        } else {
            log.warn("Skipping telemetry due to unknown benchmark version")
        }

        log.info("Full stats: " + directiveResults.encode())
        log.info("Voqal benchmark version: $benchmarkVersion")
        log.info("Model name: $modelName")
        log.info("Total cost: $" + finalResults.sumOf { it.tokenCost })
        log.info("Total prompt tokens: " + finalResults.sumOf { it.tokenUsage.promptTokens!!.toLong() })
        log.info("Total completion tokens: " + finalResults.sumOf { it.tokenUsage.completionTokens!!.toLong() })

        val successCount = finalResults.sumOf { it.successCount }
        val failCount = finalResults.sumOf { it.failCount }
        val percentage = successCount.toDouble() / (successCount + failCount).toDouble()
        log.info("Total score: $percentage (" + successCount + " of " + (successCount + failCount) + ")")
        log.info("Total time: " + finalResults.sumOf { it.timeTaken } / 1000.0 + "s")

        super.tearDown()
    }

    override fun getProjectDescriptor(): LightProjectDescriptor? {
        try {
            Class.forName("com.intellij.openapi.projectRoots.JavaSdk")
        } catch (e: ClassNotFoundException) {
            return null
        }

        return object : LightProjectDescriptor() {
            override fun getSdk(): Sdk {
                return Reflect.onClass("com.intellij.openapi.projectRoots.JavaSdk")
                    .call("getInstance")
                    .call("createJdk", "BenchmarkJdk", System.getProperty("java.home"), false)
                    .get() as Sdk
            }
        }
    }

    private abstract class DelegateEditor(val delegate: Editor) : Editor by delegate {
        abstract override fun calculateVisibleRange(): ProperTextRange
    }
}
