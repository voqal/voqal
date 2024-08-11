package dev.voqal.provider

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadRequest

/**
 * Provider that offers long-term, persistent conversational memory.
 */
@OptIn(BetaOpenAI::class)
interface AssistantProvider : AiProvider {
    override fun isAssistantProvider() = true
    suspend fun assistant(request: AssistantRequest): Assistant
    suspend fun assistant(id: AssistantId): Assistant?
    suspend fun thread(request: ThreadRequest? = null): Thread
    suspend fun thread(id: ThreadId): Thread?

    suspend fun runs(
        threadId: ThreadId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: RunId? = null,
        before: RunId? = null,
    ): List<Run>

    suspend fun message(threadId: ThreadId, messageId: MessageId): Message
    suspend fun message(threadId: ThreadId, request: MessageRequest): Message
    suspend fun messages(threadId: ThreadId, limit: Int? = null, order: SortOrder? = null): List<Message>
    suspend fun createRun(threadId: ThreadId, request: RunRequest): Run
    suspend fun getRun(threadId: ThreadId, runId: RunId): Run

    suspend fun runSteps(
        threadId: ThreadId,
        runId: RunId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: RunStepId? = null,
        before: RunStepId? = null,
    ): List<RunStep>

    suspend fun submitToolOutput(threadId: ThreadId, runId: RunId, output: List<ToolOutput>): Run

    suspend fun delete(id: AssistantId): Boolean
}
