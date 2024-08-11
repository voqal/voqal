package dev.voqal.assistant.focus

import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse

data class DirectiveExecution(
    val directive: VoqalDirective,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    val priority: Int = -1,
    var response: VoqalResponse? = null,
    val errors: MutableList<Exception> = mutableListOf()
) {

    val duration get() = endTime?.minus(startTime) ?: -1L
}
