package dev.voqal.assistant.focus

import dev.voqal.assistant.tool.VoqalTool

data class DetectedIntent(
    val intent: String,
    val args: Map<String, Any>,
    val transcript: SpokenTranscript,
    var executable: VoqalTool
) {
    val startTime = transcript.startTime
    val endTime = transcript.endTime

    fun getLoggableArgs(): Map<String, Any> {
        return args.mapValues {
            if (it.value is SpokenTranscript) {
                (it.value as SpokenTranscript).transcript
            } else {
                it.value
            }
        }
    }
}
