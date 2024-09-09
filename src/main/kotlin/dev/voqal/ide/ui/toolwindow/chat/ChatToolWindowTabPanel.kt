package dev.voqal.ide.ui.toolwindow.chat

import com.intellij.ide.scratch.ScratchRootType
import com.intellij.ide.util.RunOnceUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.Panels
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.ide.ui.VoqalUI.createScrollPaneWithSmartScroller
import dev.voqal.ide.ui.toolwindow.chat.conversation.ChatMessagePanel
import dev.voqal.ide.ui.toolwindow.chat.conversation.ChatMessageResponseBody
import dev.voqal.ide.ui.toolwindow.chat.conversation.ChatToolWindowScrollablePanel
import dev.voqal.ide.ui.toolwindow.chat.conversation.UserPromptTextArea
import dev.voqal.provider.clients.picovoice.NativesExtractor
import dev.voqal.services.*
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import java.io.IOException
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.swing.JComponent
import javax.swing.JPanel

class ChatToolWindowTabPanel(
    private val project: Project
) : Disposable {

    internal val content: JComponent
        get() = rootPanel

    private val log = project.getVoqalLogger(this::class)
    private val rootPanel: JPanel
    private val userPromptTextArea: UserPromptTextArea
    private val toolWindowScrollablePanel = ChatToolWindowScrollablePanel()

    init {
        userPromptTextArea = UserPromptTextArea { text: String -> handleSubmit(text) }
        rootPanel = createRootPanel()
        userPromptTextArea.requestFocusInWindow()
        userPromptTextArea.requestFocus()

        ApplicationManager.getApplication().invokeLater {
            addWelcomeMessage()
        }
    }

    private fun addWelcomeMessage() {
        val firstMessage = RunOnceUtil.runOnceForApp("VoqalSetup") {
            val toolWindowManager = ToolWindowManager.getInstance(project)
            val myToolWindow = toolWindowManager.getToolWindow("Voqal")
            if (myToolWindow != null) {
                project.invokeLater {
                    myToolWindow.show()
                }
            }
        }

        val message = getWelcomeMessage(firstMessage)
        val welcomeMessagePanel = createResponsePanel(false, null, null)
        toolWindowScrollablePanel.add(welcomeMessagePanel)

        val responseContainer = welcomeMessagePanel.content as ChatMessageResponseBody
        responseContainer.clear()
        responseContainer.update(message)
    }

    private fun sendMessage(message: String) {
        project.scope.launch {
            val configService = project.service<VoqalConfigService>()
            if (!configService.getConfig().pluginSettings.enabled) {
                log.warnChat("Plugin is disabled")
                return@launch
            }

            log.debug("Sending message: $message")
            project.service<VoqalDirectiveService>().handleTranscription(
                SpokenTranscript(message, null),
                textOnly = true,
                chatMessage = true
            )
        }
    }

    fun toggleDebugChatVisible() {
        toolWindowScrollablePanel.debugVisible = !toolWindowScrollablePanel.debugVisible
        toolWindowScrollablePanel.reload()
    }

    fun clearChat() {
        toolWindowScrollablePanel.clearAll()
    }

    fun addErrorMessage(message: String, speechId: String? = null) {
        val errorMessagePanel = createResponsePanel(true, speechId, null, true)
        toolWindowScrollablePanel.add(errorMessagePanel)

        val responseContainer = errorMessagePanel.content as ChatMessageResponseBody
        responseContainer.clear()
        responseContainer.update(message)
    }

    fun addUserMessage(message: String, speechId: String? = null) {
        val userMessagePanel = createResponsePanel(true, speechId, null)
        toolWindowScrollablePanel.add(userMessagePanel)

        val responseContainer = userMessagePanel.content as ChatMessageResponseBody
        responseContainer.clear()
        responseContainer.update(message)
    }

    fun addResponse(message: String, voqalResponse: VoqalResponse?) {
        val responsePanel = createResponsePanel(false, null, voqalResponse)
        toolWindowScrollablePanel.add(responsePanel)

        val responseContainer = responsePanel.content as ChatMessageResponseBody
        responseContainer.clear()
        responseContainer.update(message)
    }

    private fun createResponsePanel(
        isUser: Boolean,
        speechId: String?,
        voqalResponse: VoqalResponse?,
        isError: Boolean = false
    ): ChatMessagePanel {
        return ChatMessagePanel(
            project = project,
            isUser = isUser,
            isDebug = voqalResponse != null,
            isError = isError
        ).apply {
            if (voqalResponse != null) {
                withViewPrompt { viewPrompt(voqalResponse) }
            }
            if (isUser && speechId != null) {
                withPlayDeveloperSpeech { playDeveloperSpeech(speechId) }
            }
        }.addContent(ChatMessageResponseBody(project))
    }

    private fun playDeveloperSpeech(speechId: String) {
        val speechDirectory = File(NativesExtractor.workingDirectory, "speech")
        speechDirectory.mkdirs()
        val speechFile = File(speechDirectory, "developer-$speechId.wav")

        project.scope.launch {
            try {
                log.debug("Playing developer speech: $speechId")
                val format: AudioFormat
                val stream = AudioSystem.getAudioInputStream(speechFile)
                format = stream.format
                val info = DataLine.Info(Clip::class.java, format)
                val clip = AudioSystem.getLine(info) as Clip
                clip.open(stream)
                clip.start()
                while (!clip.isRunning) delay(10)
                while (clip.isRunning) delay(10)
                clip.close()
                stream.close()
                log.debug("Finished playing developer voice")
            } catch (e: Throwable) {
                log.warn("Failed to play developer speech: $speechId - ${e.message}")
            }
        }
    }

    private fun viewPrompt(voqalResponse: VoqalResponse) {
        project.scope.launch {
            val requestMarkdown = voqalResponse.directive.toMarkdown()
            var llmResponseJson = voqalResponse.backingResponse?.let { Json.encodePrettily(it) }
                ?: JsonObject().put("error", voqalResponse.exception?.message ?: "Unknown error").encodePrettily()
            if (llmResponseJson == "{ }" && voqalResponse.backingResponse != null) {
                log.info("No response to view. Trying kotlinx")
                try {
                    llmResponseJson = JsonObject(
                        kotlinx.serialization.json.Json.encodeToString(voqalResponse.backingResponse)
                    ).encodePrettily()
                } catch (_: Throwable) {
                }
            }

            var responseMarkdown = """
                |## Request
                |````markdown
                |${requestMarkdown}
                |````
                |
                |## Response
                |```json
                |$llmResponseJson
                |```
            """.trimMargin()
            if (voqalResponse.exception != null) {
                val llmErrorMessage = voqalResponse.exception.stackTraceToString()
                responseMarkdown += """
                    |
                    |
                    |## Error
                    |```
                    |$llmErrorMessage
                    |```
                """.trimMargin()
            }

            val scratchFile = WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                ScratchRootType.getInstance().createScratchFile(
                    project, "voqal-response.md", null, responseMarkdown
                )
            })
            if (scratchFile != null) {
                WriteCommandAction.runWriteCommandAction(project, ThrowableComputable {
                    FileEditorManager.getInstance(project).openFile(scratchFile, true)
                })
            } else {
                throw IOException("Failed to create scratch file")
            }
        }
    }

    private fun handleSubmit(text: String) {
        sendMessage(text)
    }

    private fun createUserPromptPanel(): JPanel {
        val panel = Panels.simplePanel()
        panel.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
            JBUI.Borders.empty(8)
        )
        panel.addToTop(createUserPromptTextAreaHeader())
        panel.addToBottom(userPromptTextArea)
        return panel
    }

    private fun createUserPromptTextAreaHeader(): JPanel {
        return Panels.simplePanel()
            .withBorder(JBUI.Borders.emptyBottom(8))
            .andTransparent()
    }

    private fun createRootPanel(): JPanel {
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.BOTH
        gbc.weighty = 1.0
        gbc.weightx = 1.0
        gbc.gridx = 0
        gbc.gridy = 0

        val rootPanel = JPanel(GridBagLayout())
        rootPanel.add(createScrollPaneWithSmartScroller(toolWindowScrollablePanel), gbc)

        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridy = 1
        rootPanel.add(createUserPromptPanel(), gbc)
        return rootPanel
    }

    private fun getWelcomeMessage(firstMessage: Boolean): String {
        val welcome = buildString {
            append("<html>")
            append("<p style=\"margin-top: 4px; margin-bottom: 4px;\">")
            append("Hello, my name is Voqal. I am your vocal programming assistant. ")
            append("To get started, you will need to set up my AI providers. ")
            append("You can configure my AI providers at: Settings >> Tools >> Voqal. ")
            append("Please note that I will be unable to respond until you have properly configured me.<br><br>")
            append("If you need help, please go to <a href=\"https://docs.voqal.dev\">docs.voqal.dev</a> for more information.")
            append("</p>")
            append("</html>")
        }
        if (firstMessage) return welcome

        val name = System.getProperty("user.name")
        if (name != null) {
            return """
                <html>
                <p style="margin-top: 4px; margin-bottom: 4px;">
                Hello, $name! How can I assist you today?
                </p>
                </html>
            """.trimIndent()
        }
        return """
            <html>
            <p style="margin-top: 4px; margin-bottom: 4px;">
            How can I assist you today?
            </p>
            </html>
        """.trimIndent()
    }

    override fun dispose() = Unit
}
