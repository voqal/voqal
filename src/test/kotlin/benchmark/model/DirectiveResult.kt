package benchmark.model

import com.aallam.openai.api.core.Usage
import io.vertx.core.json.JsonObject

data class DirectiveResult(
    val benchmarkVersion: String,
    val testName: String,
    val modelName: String,
    val successCount: Int,
    val failCount: Int,
    val tokenUsage: Usage,
    val tokenCost: Double,
    val timeTaken: Long,
    val suite: String,
    val tools: List<String>,
    val successMessages: List<String>,
    val failMessages: List<String>,
    val completionText: String,
    val directiveId: String,
    val language: String,
    val errors: List<Exception>
) {
    fun toJson(): JsonObject {
        val json = JsonObject()
        json.put("benchmarkVersion", benchmarkVersion)
        json.put("testName", testName)
        json.put("modelName", modelName)
        json.put("successCount", successCount)
        json.put("failCount", failCount)
        json.put(
            "tokenUsage", JsonObject()
                .put("promptTokens", tokenUsage.promptTokens)
                .put("completionTokens", tokenUsage.completionTokens)
                .put("totalTokens", tokenUsage.totalTokens)
        )
        json.put("tokenCost", tokenCost)
        json.put("timeTaken", timeTaken)
        json.put("suite", suite)
        json.put("tools", tools)
        json.put("successMessages", successMessages)
        if (failMessages.isNotEmpty()) {
            json.put("failMessages", failMessages)
        }
        json.put("completionText", completionText)
        json.put("directiveId", directiveId)
        json.put("language", language)
        if (errors.isNotEmpty()) {
            json.put("errors", errors.map { it.stackTraceToString() })
        }
        return json
    }
}