package dev.voqal.assistant.memory.thread

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.*
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.messageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.*
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.api.vectorstore.VectorStoreId
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.memory.MemorySlice
import dev.voqal.assistant.memory.MemorySystem
import dev.voqal.assistant.tool.ide.ShowCodeTool
import dev.voqal.provider.AssistantProvider
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.delay
import java.util.*

/**
 * Memory system that stores the chat messages via Assistant API.
 */
@OptIn(BetaOpenAI::class)
class ThreadMemorySystem(
    private val project: Project
) : MemorySystem, MemorySlice {

    override val id = UUID.randomUUID().toString()

    private val log = project.getVoqalLogger(this::class)
    private lateinit var assistant: Assistant
    private lateinit var threadId: String

    private suspend fun setupAssistant(directive: VoqalDirective) {
        if (::assistant.isInitialized) return
        val assistantId = directive.assistant.promptSettings?.assistantId ?: ""
        if (assistantId.isNotEmpty()) {
            log.info("Loading existing assistant")
            getAssistant(assistantId)
        } else {
            log.info("Creating new assistant")
            createAssistant(directive)
        }
    }

    private suspend fun setupThread(directive: VoqalDirective) {
        if (::threadId.isInitialized) return
        val threadId = directive.assistant.promptSettings?.assistantThreadId ?: ""
        if (threadId.isNotEmpty()) {
            log.info("Loading existing thread")
            getThread(threadId)
        } else {
            log.info("Creating new assistant thread")
            createThread()
        }
    }

    private suspend fun waitTillThreadReady() {
        val openAI = project.service<VoqalConfigService>().getAiProvider().asAssistantProvider()
        var wait = true
        while (wait) {
            val threadRuns = openAI.runs(ThreadId(threadId))
            log.info("Found runs: ${threadRuns.map { it.status }}")

            if (threadRuns.none { it.status == Status.InProgress }) {
                wait = false
            } else {
                delay(100)
            }
        }
    }

    override fun getMemorySlice() = this

    override suspend fun addMessage(directive: VoqalDirective, addMessage: Boolean): VoqalResponse {
        setupAssistant(directive)
        setupThread(directive)
        waitTillThreadReady()

        val config = project.service<VoqalConfigService>().getConfig()
        val aiProvider = project.service<VoqalConfigService>().getAiProvider()
        if (!aiProvider.isAssistantProvider()) {
            log.warn("Missing assistant provider. Config: $config")
            throw IllegalStateException("Missing assistant provider")
        }
        val assistantProvider = aiProvider.asAssistantProvider()
//        val systemMessage = ChatMessage(
//            ChatRole.User,
//            directive.toMarkdown()
//        ) //todo: should be system role
//        assistantProvider.message(ThreadId(threadId), messageRequest {
//            content = systemMessage.content
//            role = systemMessage.role
//        })
        val userMessage = ChatMessage(ChatRole.User, directive.developer.transcription)
        assistantProvider.message(ThreadId(threadId), messageRequest {
            content = userMessage.content
            role = userMessage.role
        })

        var threadRun = assistantProvider.createRun(ThreadId(threadId), runRequest { assistantId = assistant.id })
        while (threadRun.status !in listOf(Status.RequiresAction, Status.Completed)) {
            delay(250)
            threadRun = assistantProvider.getRun(threadRun.threadId, threadRun.id)
            log.info("Thread run status: " + threadRun.status)
            if (threadRun.status == Status.Failed) {
                throw IllegalStateException(threadRun.status.value)
            }
        }

        val messages = assistantProvider.messages(ThreadId(threadId), limit = 4)
        log.info("Got messages: " + messages.size)
        val textContent = (messages.first().content.first() as MessageContent.Text).text.value
        return handleTextContent(textContent, directive)

//        val steps = assistantProvider.runSteps(threadRun.threadId, threadRun.id)
//        log.debug(steps.toString())
//        if (steps.size > 1) {
//            log.warn("multiple steps: " + steps.size)
//        }
//        val stepDetails = steps.first().stepDetails //todo: handle multiple steps
//        return when (stepDetails) {
//            is MessageCreationStepDetails -> handleMessageResponse(directive, assistantProvider, threadRun, stepDetails)
//            is ToolCallStepDetails -> handleToolCallResponse(directive, stepDetails, threadRun)
//        }
    }

    private suspend fun handleToolCallResponse(
        directive: VoqalDirective,
        stepDetails: ToolCallStepDetails,
        threadRun: Run
    ): VoqalResponse {
        log.info("Handling tool call response: $stepDetails")
        if ((stepDetails.toolCalls?.size ?: 0) > 1) {
            //todo: handle multiple tool calls
            log.warn("multiple tool calls: " + stepDetails.toolCalls?.size)
        }
        if (stepDetails.toolCalls?.size == 1) {
            val toolCall = stepDetails.toolCalls!!.first() as ToolCallStep.FunctionTool
            val openAI = project.service<VoqalConfigService>().getAiProvider().asAssistantProvider()
            val run = openAI.submitToolOutput(
                threadRun.threadId,
                threadRun.id,
                listOf(ToolOutput(ToolId(toolCall.id.id), "ok"))
            )
            log.info("Submitted tool output: ${run.copy(instructions = null)}")

            waitTillThreadReady()
            val finalStatus = openAI.getRun(run.threadId, run.id).copy(instructions = null)
            if (finalStatus.status != Status.Completed) {
                log.warn("Final status: $finalStatus")
            }
        }

        //todo: this
        val toolCalls = stepDetails.toolCalls?.map {
            val func = it as ToolCallStep.FunctionTool
            ToolCall.Function(
                id = ToolId(func.id.id),
                function = FunctionCall(
                    nameOrNull = func.function.name,
                    argumentsOrNull = func.function.arguments
                )
            )
        }
        return VoqalResponse(
            directive,
            toolCalls ?: emptyList(), ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = "todo"
                            )
                        )
                    )
                )
            )
        )
    }

    private suspend fun handleMessageResponse(
        directive: VoqalDirective,
        openAI: AssistantProvider,
        threadRun: Run,
        stepDetails: MessageCreationStepDetails
    ): VoqalResponse {
        log.info("Handling message response: $stepDetails")
        val responseMessage = openAI.message(threadRun.threadId, stepDetails.messageCreation.messageId)
        val textContent = (responseMessage.content.first() as MessageContent.Text).text.value
        return handleTextContent(textContent, directive)
    }

    private fun handleTextContent(
        textContent: String,
        directive: VoqalDirective
    ): VoqalResponse {
        //todo: this
        val toolCalls = listOf(
            ToolCall.Function(
                id = ToolId(ShowCodeTool.NAME),
                function = FunctionCall(
                    nameOrNull = ShowCodeTool.NAME,
                    argumentsOrNull = JsonObject().put("result", textContent).toString()
                )
            )
        )
        return VoqalResponse(
            directive,
            toolCalls, ChatCompletion(
                id = "n/a",
                created = System.currentTimeMillis(),
                model = ModelId("n/a"),
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        ChatMessage(
                            ChatRole.Assistant,
                            TextContent(
                                content = textContent
                            )
                        )
                    )
                )
            )
        )
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun createAssistant(directive: VoqalDirective) {
        val promptSettings = project.service<VoqalConfigService>().getCurrentPromptSettings()
        val vectorStoreId = promptSettings.vectorStoreId
        if (vectorStoreId == "") {
            TODO()
        }

        val instructions = project.service<VoqalConfigService>().getPromptTemplate(promptSettings)
        val request = AssistantRequest(
            name = project.name + "-code-search", //todo
            tools = listOf(AssistantTool.FileSearch),
            model = ModelId("gpt-4o-mini"), //todo
            instructions = instructions,
            toolResources = ToolResources(FileSearchResources(listOf(VectorStoreId(vectorStoreId))))
        )

        val configService = project.service<VoqalConfigService>()
        val openAI = configService.getAiProvider().asAssistantProvider()
        assistant = openAI.assistant(request)
        log.info("Created new assistant: ${assistant.id.id}")

        val updatedPromptSettings = promptSettings.copy(assistantId = assistant.id.id)
        configService.updateConfig(updatedPromptSettings)
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun getAssistant(assistantId: String) {
        val openAI = project.service<VoqalConfigService>().getAiProvider().asAssistantProvider()
        val existingAssistant = openAI.assistant(AssistantId(assistantId))
        if (existingAssistant == null) {
            TODO()
        }
        assistant = existingAssistant
        log.info("Using existing assistant: $assistantId")
    }

    private suspend fun createThread() {
        val configService = project.service<VoqalConfigService>()
        val promptSettings = project.service<VoqalConfigService>().getCurrentPromptSettings()
        val vectorStoreId = promptSettings.vectorStoreId
        if (vectorStoreId == "") {
            TODO()
        }
        val openAI = configService.getAiProvider().asAssistantProvider()
        val thread = openAI.thread(
            ThreadRequest(toolResources = ToolResources(FileSearchResources(listOf(VectorStoreId(vectorStoreId)))))
        )

        this.threadId = thread.id.id
        log.info("Created thread: ${thread.id}")

        val updatedPromptSettings = promptSettings.copy(assistantThreadId = thread.id.id)
        configService.updateConfig(updatedPromptSettings)
    }

    private suspend fun getThread(threadId: String): Thread {
        val openAI = project.service<VoqalConfigService>().getAiProvider().asAssistantProvider()
        val thread = openAI.thread(ThreadId(threadId))
        if (thread == null) {
            TODO()
        }

        this.threadId = thread.id.id
        log.info("Using existing thread: $threadId")
        return thread
    }
}
