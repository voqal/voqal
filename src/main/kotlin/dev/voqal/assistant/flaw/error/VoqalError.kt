package dev.voqal.assistant.flaw.error

import com.aallam.openai.api.chat.ChatCompletion
import dev.voqal.assistant.flaw.VoqalFlaw

abstract class VoqalError(
    val completion: ChatCompletion
) : Exception(), VoqalFlaw
