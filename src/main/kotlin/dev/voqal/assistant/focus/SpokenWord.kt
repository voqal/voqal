package dev.voqal.assistant.focus

import io.vertx.core.json.JsonObject

data class SpokenWord(
    val word: String,
    val startTime: Double,
    val endTime: Double,
    val confidence: Double,
    val punctuatedWord: String? = null,
    val syntheticWord: Boolean = false,
    var isFinal: Boolean = false
) {

    constructor(json: JsonObject) : this(
        json.getString("word"),
        json.getDouble("startTime"),
        json.getDouble("endTime"),
        json.getDouble("confidence"),
        json.getString("punctuatedWord"),
        json.getBoolean("syntheticWord") ?: false,
        json.getBoolean("isFinal") ?: false
    )

    fun isOverlapping(other: SpokenWord): Boolean {
        return !(endTime <= other.startTime || startTime >= other.endTime)
    }

    fun toJson(): JsonObject {
        return JsonObject().apply {
            put("word", word)
            put("startTime", startTime)
            put("endTime", endTime)
            put("confidence", confidence)
            punctuatedWord?.let { put("punctuatedWord", it) }
            put("syntheticWord", syntheticWord)
            put("isFinal", isFinal)
        }
    }
}
