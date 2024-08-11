package dev.voqal.assistant.flaw.error.tool

import com.aallam.openai.api.chat.ChatCompletion
import dev.voqal.assistant.flaw.error.VoqalError

abstract class ToolError(completion: ChatCompletion) : VoqalError(completion)
