package dev.voqal.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.util.concurrency.ThreadingAssertions
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.code.ViewingCode

/**
 * Used to ensure LLM prompts do not contain ignored files or exceed token limits.
 */
@Service(Service.Level.PROJECT)
class VoqalContextService(private val project: Project) {

    private var registry = Encodings.newDefaultEncodingRegistry()
    private var enc = registry.getEncoding(EncodingType.CL100K_BASE)

    fun getTokenCount(text: String): Int {
        return enc.countTokens(text)
    }

    fun getOpenFiles(selectedTextEditor: Editor?): MutableList<ViewingCode> {
        val openVirtualFiles = ReadAction.compute(ThrowableComputable {
            FileEditorManager.getInstance(project).openFiles.filter {
                it.path != selectedTextEditor?.virtualFile?.path && //ignore the current file
                        !it.name.contains("voqal-response") &&//ignore voqal responses
                        !it.path.contains(".voqal") && //ignore voqal files
                        !ChangeListManager.getInstance(project).isIgnoredFile(it) //ignore VCS ignored files
            }
        })
        val openFiles = ReadAction.compute(ThrowableComputable {
            openVirtualFiles.mapNotNull {
                val fileType = FileTypeManager.getInstance().getFileTypeByFile(it)
                val language = (fileType as? LanguageFileType)?.language
                if (language != null) {
                    ViewingCode(
                        code = FileDocumentManager.getInstance().getDocument(it)?.text,
                        language = language.id,
                        filename = it.name
                    )
                } else {
                    null
                }
            }
        }).toMutableList()
        return openFiles
    }

    fun getSelectedTextEditor(): Editor? {
        var selectedTextEditor = FileEditorManager.getInstance(project).selectedTextEditor
        if (selectedTextEditor == null) {
            val virtualFile = FileEditorManager.getInstance(project).selectedEditor?.file
            selectedTextEditor = EditorFactory.getInstance().allEditors.find { it.virtualFile == virtualFile }
        }
        if (selectedTextEditor?.virtualFile?.name?.contains("voqal-response") == true) {
            selectedTextEditor = null
        } else if (selectedTextEditor?.virtualFile?.path?.contains(".voqal") == true) {
            selectedTextEditor = null
        } else if (selectedTextEditor?.virtualFile?.let {
                ChangeListManager.getInstance(project).isIgnoredFile(it)
            } == true) {
            selectedTextEditor = null
        }
        return selectedTextEditor
    }

    fun cropAsNecessary(fullDirective: VoqalDirective): VoqalDirective {
        val log = project.getVoqalLogger(this::class)
        ThreadingAssertions.assertBackgroundThread()
        var croppedCommand = fullDirective
        val openFiles = croppedCommand.developer.openFiles.toMutableList()
        val relevantFiles = croppedCommand.developer.relevantFiles //already mutable
        val currentCode = croppedCommand.developer.viewingCode?.code
        val selectedTextEditor = croppedCommand.ide.editor

        //count tokens
        var tokenLimit = fullDirective.assistant.languageModelSettings.tokenLimit
        if (tokenLimit == -1) {
            log.warn("Token limit not set. Using no limit")
            tokenLimit = Integer.MAX_VALUE
        }
        val commandPrompt = croppedCommand.toMarkdown()
        var promptTokens = enc.countTokens(commandPrompt)
        if (promptTokens > tokenLimit * 0.9) {
            log.debug("Initial command prompt tokens: $promptTokens")
        }

        //first see if project structure is taking over 50% of tokens and reduce if necessary
        if (promptTokens > tokenLimit * 0.9 && croppedCommand.ide.projectFileTree != null) {
            val projectStructure = project.service<VoqalSearchService>().getProjectStructureAsMarkdownTree()
            val projectStructureTokens = enc.countTokens(projectStructure)
            if (projectStructureTokens > tokenLimit * 0.25) {
                log.debug("Cropping project structure to reduce tokens")
                croppedCommand = croppedCommand.copy(
                    ide = IdeContext(
                        project,
                        selectedTextEditor,
                        projectFileTree = project.service<VoqalSearchService>()
                            .getProjectStructureAsMarkdownTree(enc, (tokenLimit * 0.25).toInt())
                    )
                )
                promptTokens = enc.countTokens(croppedCommand.toMarkdown())
            }
        }

        if (promptTokens > tokenLimit * 0.9 && openFiles.isNotEmpty()) {
            log.debug("Removing open files to reduce tokens")

            //reduce open files context till below 90%
            while (openFiles.isNotEmpty() && promptTokens > tokenLimit * 0.9) {
                openFiles.removeAt(openFiles.size - 1)
                val newPrompt = croppedCommand.copy(
                    developer = croppedCommand.developer.copy(openFiles = openFiles)
                ).toMarkdown()
                val newTokens = enc.countTokens(newPrompt)
                if (newTokens < promptTokens) {
                    promptTokens = newTokens
                    croppedCommand = croppedCommand.copy(
                        developer = croppedCommand.developer.copy(openFiles = openFiles)
                    )
                }
            }
        }

        //if still not below 90%, remove relevant files
        if (promptTokens > tokenLimit * 0.9 && relevantFiles.isNotEmpty()) {
            log.debug("Removing relevant files to reduce tokens")

            //reduce relevant files context till below 90%
            while (relevantFiles.isNotEmpty() && promptTokens > tokenLimit * 0.9) {
                relevantFiles.removeAt(relevantFiles.size - 1)
                val newPrompt = croppedCommand.toMarkdown()
                promptTokens = enc.countTokens(newPrompt)
            }
        }

        //if still not below 90%, add viewing code text till 90% limit
        if (promptTokens > tokenLimit * 0.9 && selectedTextEditor != null && currentCode != null) {
            log.debug("Cropping viewing code to reduce tokens")
            var visibleRange: ProperTextRange? = null
            ApplicationManager.getApplication().invokeAndWait {
                visibleRange = selectedTextEditor.calculateVisibleRange()
            }
            var startOffset = visibleRange!!.startOffset
            var endOffset = visibleRange!!.endOffset
            var reduced = false

            do {
                val newViewingCode = ViewingCode(code = currentCode.substring(startOffset, endOffset))
                val newPrompt = croppedCommand.copy(
                    developer = croppedCommand.developer.copy(viewingCode = newViewingCode)
                ).toMarkdown()
                val newTokens = enc.countTokens(newPrompt)
                if (newTokens < tokenLimit * 0.9) {
                    reduced = true
                    promptTokens = newTokens
                    croppedCommand = croppedCommand.copy(
                        developer = croppedCommand.developer.copy(viewingCode = newViewingCode)
                    )

                    val deltaAmount = currentCode.length * 0.01
                    if (startOffset > 0) {
                        startOffset = (startOffset - deltaAmount.toInt()).coerceAtLeast(0)
                    }
                    if (endOffset < currentCode.length) {
                        endOffset = currentCode.length.coerceAtMost(endOffset + deltaAmount.toInt())
                    }
                } else break
            } while (promptTokens < tokenLimit * 0.9)

            if (!reduced) {
                log.warn("Unable to reduce tokens. Removing all context. Consider increasing token limit greater than: $tokenLimit")
                croppedCommand = croppedCommand.copy(
                    developer = croppedCommand.developer.copy(
                        openFiles = listOf(),
                        viewingCode = null
                    )
                )
            }
        }
        log.debug("Command prompt tokens: $promptTokens")
        return croppedCommand
    }
}
