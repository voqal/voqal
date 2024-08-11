package dev.voqal.assistant.flaw.error.parse

import com.aallam.openai.api.chat.ChatCompletion
import dev.voqal.assistant.flaw.error.VoqalError

class ResponseParseError(
    completion: ChatCompletion,
    override val message: String
) : VoqalError(completion)
