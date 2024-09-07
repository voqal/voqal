package benchmark.model

import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.Usage
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.flaw.error.VoqalError
import dev.voqal.services.getVoqalLogger
import io.vertx.core.Future
import io.vertx.core.Promise
import kotlin.reflect.KCallable

class BenchmarkPromise(
    val project: Project,
    val benchmarkVersion: String,
    val instance: Any,
    val callable: KCallable<*>,
    val modelName: String,
    val suite: String
) {

    var testName = callable.name

    private val log = project.getVoqalLogger(this::class)
    private var floorStartTime: Long = -1
    private var floorStopTime: Long = -1
    private var successCount = 0
    private var failCount = 0
    private val successStrs = mutableListOf<String>()
    private val failStrs = mutableListOf<String>()
    private val resultPromise = Promise.promise<DirectiveResult>()
    val promise = Promise.promise<BenchmarkPromise>()
    var directiveId: String? = null
    var response: VoqalResponse? = null
    var errors: List<Exception>? = null

    fun success(s: String) {
        successStrs.add(s)
        successCount++
    }

    fun fail(s: String) {
        failStrs.add(s)
        failCount++
    }

    fun testFinished() {
        val tokenUsage = if (response != null) {
            response!!.backingResponse!!.usage!!
        } else {
            var promptTokens = 0
            var completionTokens = 0
            var totalTokens = 0
            errors?.forEach {
                if (it is VoqalError) {
                    promptTokens += it.completion.usage?.promptTokens ?: 0
                    completionTokens += it.completion.usage?.completionTokens ?: 0
                    totalTokens += it.completion.usage?.totalTokens ?: 0
                }
            }

            Usage(promptTokens, completionTokens, totalTokens)
        }
        val tokenCost = if (response != null) {
            response!!.getSpentCurrency()
        } else {
            try {
                VoqalResponse.calculateTotalPrice(
                    modelName,
                    tokenUsage.promptTokens!!,
                    tokenUsage.completionTokens!!
                )
            } catch (e: Exception) {
                log.warn("Model $modelName not found in the price table")
                -1.0
            }
        }

        val result = DirectiveResult(
            benchmarkVersion = benchmarkVersion,
            testName = testName,
            modelName = modelName,
            successCount = successCount,
            failCount = failCount,
            tokenUsage = tokenUsage,
            tokenCost = tokenCost,
            timeTaken = floorStopTime - floorStartTime,
            suite = suite,
            tools = response?.toolCalls?.map { (it as ToolCall.Function).function.name } ?: emptyList(),
            successMessages = successStrs,
            failMessages = failStrs,
            completionText = response?.getBackingResponseAsText() ?: "n/a",
            directiveId = directiveId ?: "n/a",
            language = System.getenv("VQL_LANG"),
            errors = errors ?: emptyList()
        )
        resultPromise.complete(result)
    }

    fun future(): Future<DirectiveResult> {
        return resultPromise.future()
    }

    fun startFloorTime() {
        require(floorStartTime == -1L)
        floorStartTime = System.currentTimeMillis()
    }

    fun stopFloorTime() {
        require(floorStopTime == -1L)
        floorStopTime = System.currentTimeMillis()
    }
}
