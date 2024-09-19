package benchmark

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.io.File

fun main() {
    outputResults("all")
    outputResults("edit_mode")
    outputResults("idle_mode")
}

private fun outputResults(mode: String) {
    val benchmarkResults = JsonArray()
    File("benchmark").listFiles { _, name ->
        name.startsWith(mode) && name.endsWith(".json")
    }?.forEach {
        val result = JsonObject(it.readText())
        val keyResults = JsonObject().put("modelName", result.getString("modelName"))
        benchmarkResults.add(keyResults)
        val results = JsonArray()
        results.add(result.getJsonArray("results"))
        keyResults.put("results", results)
    }
    if (!benchmarkResults.isEmpty) {
        outputChart(mode, benchmarkResults)
    }
}

private fun outputChart(mode: String, benchmarkResults: JsonArray) {
    val finalData = JsonArray()
    benchmarkResults.forEach {
        val thisArray = JsonArray()
        val results = it as JsonObject
        val resultsArray = results.getJsonArray("results")
        resultsArray.forEachIndexed { runNumber, it ->
            val results = it as JsonArray
            val modelName = results.getJsonObject(0).getString("modelName")

            val language = results.getJsonObject(0).getString("language")
            val timeTaken = results.sumOf {
                (it as JsonObject).getInteger("timeTaken")
            }
            val successCount = results.sumOf {
                (it as JsonObject).getInteger("successCount")
            }
            val failCount = results.sumOf {
                (it as JsonObject).getInteger("failCount")
            }
            val score = successCount / (successCount + failCount).toDouble()
            val tokenCost = results.sumOf {
                (it as JsonObject).getDouble("tokenCost")
            }
            thisArray.add(
                JsonArray().add(timeTaken).add(score).add(tokenCost).add(language).add(modelName)
            )

            println("$modelName ($language): $timeTaken,$score,$tokenCost")
        }
        finalData.add(thisArray)
    }
    println("\n\n")

    //order by model name
    val data = JsonArray()
    finalData.toList().sortedBy {
        (it as JsonArray).getJsonArray(0).getString(4)
    }.forEach {
        data.add(it)
    }
    val modelNames = mutableListOf<String>()
    data.forEach {
        modelNames.add((it as JsonArray).getJsonArray(0).getString(4))
    }

    val series = JsonArray()
    modelNames.forEachIndexed { index, modelName ->
        series.add(
            JsonObject()
                .put("name", modelName)
                .put("data", data.getJsonArray(index))
                .put("type", "scatter")
                .put("symbolSize", "function(data) { return Math.sqrt(data[2] * 10000); }")
                .put(
                    "emphasis", JsonObject()
                        .put("focus", "series")
                        .put(
                            "label", JsonObject()
                                .put("show", true)
                                .put("formatter", "function(param) { return param.data[3]; }")
                                .put("position", "top")
                        )
                )
        )
    }
    val option = JsonObject()
        .put(
            "legend", JsonObject()
                .put("top", "3%")
                .put("data", JsonArray(modelNames))
        )
        .put(
            "grid", JsonObject()
                .put("left", "8%")
                .put("top", "12%")
        )
        .put(
            "xAxis", JsonObject()
                .put(
                    "splitLine", JsonObject()
                        .put(
                            "lineStyle", JsonObject()
                                .put("type", "dashed")
                        )
                )
                .put("scale", true)
        )
        .put(
            "yAxis", JsonObject()
                .put(
                    "splitLine", JsonObject()
                        .put(
                            "lineStyle", JsonObject()
                                .put("type", "dashed")
                        )
                )
                .put("scale", true)
                .put("max", 1.0)
        )
        .put("series", series)

    var jsFile = "option = ${option.encodePrettily()};"
    jsFile = jsFile
        .replace(
            "\"function(data) { return Math.sqrt(data[2] * 10000); }\"",
            "function(data) { return Math.sqrt(data[2] * 10000); }"
        )
        .replace("\"function(param) { return param.data[3]; }\"", "function(param) { return param.data[3]; }")

    File("benchmark", "$mode.js").writeText(jsFile)
}
