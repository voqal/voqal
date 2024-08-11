package dev.voqal.assistant.context

import dev.voqal.assistant.flaw.error.VoqalError

data class FlawContext(
    val previousErrors: MutableList<VoqalError> = mutableListOf()
) : VoqalContext
