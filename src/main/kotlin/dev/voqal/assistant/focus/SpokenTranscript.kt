package dev.voqal.assistant.focus

import io.vertx.core.json.JsonObject

//todo: alternatives
data class SpokenTranscript(
    val transcript: String,
    val speechId: String?,
    val words: List<SpokenWord> = emptyList(),
    val isClean: Boolean = false,
    val isIntent: Boolean = false,
    val isFinal: Boolean = false
) {

    constructor(json: JsonObject) : this(
        json.getString("transcript"),
        json.getString("speechId"),
        json.getJsonArray("words")?.map { SpokenWord(it as JsonObject) } ?: emptyList(),
        json.getBoolean("isClean") ?: false,
        json.getBoolean("isIntent") ?: false,
        json.getBoolean("isFinal") ?: false
    )

    val startTime = words.firstOrNull()?.startTime ?: -1.0
    val endTime = words.lastOrNull()?.endTime ?: -1.0

    val cleanTranscript: String
        get() {
            return if (isClean) {
                transcript
            } else {
                //replace punctuation and lowercase
                transcript.trim().replace(Regex("[^A-Za-z0-9 ]"), "").lowercase()
            }
        }

    fun toJson(): JsonObject {
        return JsonObject().apply {
            put("transcript", transcript)
            speechId?.let { put("speechId", it) }
            put("words", words.map { it.toJson() })
            put("isClean", isClean)
            put("isIntent", isIntent)
            put("isFinal", isFinal)
        }
    }

    //output non-default values
    override fun toString(): String {
        return buildString {
            append("SpokenTranscript(")
            append("transcript=$transcript, ")
            if (speechId != null) append("speechId=$speechId, ")
            if (words.isNotEmpty()) append("words=$words, ")
            if (isClean) append("isClean=$isClean, ")
            if (isIntent) append("isIntent=$isIntent, ")
            if (isFinal) append("isFinal=$isFinal, ")
            if (startTime != -1.0) append("startTime=$startTime, ")
            if (endTime != -1.0) append("endTime=$endTime, ")

            if (endsWith(", ")) setLength(length - 2)
            append(")")
        }
    }
}
