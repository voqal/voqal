package dev.voqal.services

import com.intellij.formatting.Block
import com.intellij.formatting.FormattingContext
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.lang.LanguageFormatting
import com.intellij.lang.LanguageStructureViewBuilder
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
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.util.concurrency.ThreadingAssertions
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingType
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.context.code.ViewingCode

/**
 * Used to ensure LLM prompts do not contain ignored files or exceed token limits.
 */
@Service(Service.Level.PROJECT)
class VoqalContextService(private val project: Project) {

    private val log = project.getVoqalLogger(this::class)
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
            val projectStructure = getProjectStructureAsMarkdownTree()
            val projectStructureTokens = enc.countTokens(projectStructure)
            if (projectStructureTokens > tokenLimit * 0.25) {
                log.debug("Cropping project structure to reduce tokens")
                croppedCommand = croppedCommand.copy(
                    ide = IdeContext(
                        project,
                        selectedTextEditor,
                        projectFileTree = getProjectStructureAsMarkdownTree(enc, (tokenLimit * 0.25).toInt())
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

    /**
     * Returns markdown representation of the tree.
     * ```
     * ├── src
     * │   ├── main
     * │   │   ├── kotlin
     * ```
     */
    fun getProjectStructureAsMarkdownTree(encoding: Encoding? = null, tokenLimit: Int = -1): String {
        val sb = StringBuilder()
        val projectDir = project.service<VoqalSearchService>().getProjectRoot()
        sb.append(recursiveMarkdownTree(projectDir.children.toList()))

        //remove from bottom and top (till under tokenLimit then add ... to top and bottom to indicate cropped)
        val tokens = encoding?.countTokens(sb.toString()) ?: -1
        if (tokens > tokenLimit) {
            val lines = sb.toString().lines().toMutableList()
            //take from bottom and top equally till under tokenLimit
            while (encoding!!.countTokens(sb.toString()) > tokenLimit && lines.size > 2) {
                lines.removeAt(0)
                lines.removeAt(lines.size - 1)
            }
            return (listOf("...") + lines + listOf("...")).joinToString("\n")
        }
        return sb.toString()
    }

    private fun recursiveMarkdownTree(children: Collection<VirtualFile>, level: Int = 0): String {
        val ignoreNames = setOf(
            ".git", ".idea", ".gitignore", "build", "out", "target", "node_modules",
            "__pycache__", "venv", ".gradle", "obj"
        )
        val ignoreExtensions = setOf("class")

        val sb = StringBuilder()
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        for (child in children.sortedBy { it.name }) {
            val isInContent = ReadAction.compute(ThrowableComputable {
                projectFileIndex.isInContent(child)
            })
            if (!isInContent || child.name in ignoreNames || child.extension in ignoreExtensions) {
                continue
            } else if (child.name == "obj" && child.children.any { it.name == "Debug" || it.name == "Release" }) {
                continue //.NET build dir
            }

            repeat(level) {
                sb.append("│   ")
            }
            sb.append("├── ")
            sb.append(child.name)
            sb.append("\n")
            if (child.isDirectory) {
                sb.append(recursiveMarkdownTree(child.children.toList(), level + 1))
            }
        }
        return sb.toString()
    }

    fun getProjectCodeStructure(encoding: Encoding? = null, tokenLimit: Int = -1): String {
        val sb = StringBuilder()
        val projectDir = project.service<VoqalSearchService>().getProjectRoot()
        sb.append(recursiveCodeStructure(projectDir.children.toList()))

        //remove final "---" if present
        if (sb.endsWith("---\n")) {
            sb.delete(sb.length - 4, sb.length)
            sb.append("\n")
        }

        //remove from bottom and top (till under tokenLimit then add ... to top and bottom to indicate cropped)
        val tokens = encoding?.countTokens(sb.toString()) ?: -1
        if (tokens > tokenLimit) {
            val lines = sb.toString().lines().toMutableList()
            //take from bottom and top equally till under tokenLimit
            while (encoding!!.countTokens(sb.toString()) > tokenLimit && lines.size > 2) {
                lines.removeAt(0)
                lines.removeAt(lines.size - 1)
            }
            return (listOf("...") + lines + listOf("...")).joinToString("\n")
        }
        return sb.toString()
    }

    private fun recursiveCodeStructure(children: Collection<VirtualFile>, level: Int = 0): String {
        val ignoreNames = setOf(
            ".git", ".idea", ".gitignore", "build", "out", "target", "node_modules",
            "__pycache__", "venv", ".gradle", "obj"
        )
        val ignoreExtensions = setOf("class")

        val sb = StringBuilder()
        val projectFileIndex = ProjectFileIndex.getInstance(project)
        for (child in children.sortedBy { it.name }) {
            val isInContent = ReadAction.compute(ThrowableComputable {
                projectFileIndex.isInContent(child)
            })
            if (!isInContent || child.name in ignoreNames || child.extension in ignoreExtensions) {
                continue
            } else if (child.name == "obj" && child.children.any { it.name == "Debug" || it.name == "Release" }) {
                continue //.NET build dir
            }

            if (child.isDirectory) {
                sb.append(recursiveCodeStructure(child.children.toList(), level + 1))
            } else {
//                val code = summarizeCode(child)
//                if (code != null) {
//                    sb.append(code)
//                    sb.append("---\n")
//                }

                val fileText = ReadAction.compute(ThrowableComputable {
                    child.readText()
                })
                sb.append(fileText).append("---\n")
            }
        }
        return sb.toString()
    }

    /**
     * Summarizes file by returning a string of structure signatures.
     * Example:
     * ```kotlin
     * MyClass.kt
     *   class MyClass
     *     fun add(a: Int, b: Int): Int
     *       ...
     *     fun subtract(a: Int, b: Int): Int
     *       ...
     * ```
     */
    private fun summarizeCode(virtualFile: VirtualFile): String? {
        val psiFile = ReadAction.compute(ThrowableComputable { PsiManager.getInstance(project).findFile(virtualFile) })
        if (psiFile == null) {
            log.warn("Failed to find PSI file for virtual file: $virtualFile")
            return null
        }

        //ignore non-code files
        if (!psiFile.isFile()) {
            log.debug("Ignoring non-code file: $virtualFile")
            return null
        }

        val structure = ReadAction.compute(ThrowableComputable {
            LanguageStructureViewBuilder.INSTANCE.getStructureViewBuilder(psiFile)
        })
        if (structure == null) {
            log.warn("Failed to get structure view builder for file: $virtualFile")
            return null
        }

        val sb = StringBuilder()
        val structureView = structure.createStructureView(null, project)
        ReadAction.compute(ThrowableComputable {
            recursiveSummarizeCode((structureView.treeModel).root, sb)
        })
        Disposer.dispose(structureView)
        return sb.toString()
    }

    private fun recursiveSummarizeCode(element: StructureViewTreeElement, sb: StringBuilder, indent: String = "") {
        val psiElement = element.value as PsiElement
        if (psiElement.isFile()) {
            sb.append((psiElement as PsiNamedElement).name).append("\n")
        } else if (psiElement.isClass()) {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            }
            //todo: isStatic

            sb.append("class ").append(element.presentation.presentableText).append("\n")
            //sb.append(indent).append(indent).append("...").append("\n")
        } else if (psiElement.isFunction()) {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            } else if (presentationIcon.toString().contains("protected.svg")) {
                sb.append("protected ")
            } else if (presentationIcon.toString().contains("plocal.svg")) {
                sb.append("package-private ")
            }
            //todo: isStatic

            if (psiElement.isJvm()) {
                sb.append("function ")
            } else if (psiElement.isPython()) {
                sb.append("def ")
            } else if (psiElement.isGo()) {
                sb.append("func ")
            }
            sb.append(element.presentation.presentableText).append("\n")
                .append(indent).append(indent).append("...").append("\n")
        } else {
            sb.append(indent)

            val presentationIcon = element.presentation.getIcon(false)
            if (presentationIcon.toString().contains("public.svg")) {
                sb.append("public ")
            } else if (presentationIcon.toString().contains("private.svg")) {
                sb.append("private ")
            } else if (presentationIcon.toString().contains("protected.svg")) {
                sb.append("protected ")
            } else if (presentationIcon.toString().contains("plocal.svg")) {
                sb.append("package-private ")
            }
            sb.append(element.presentation.presentableText).append("\n")
        }

        for (child in element.children) {
            recursiveSummarizeCode(child as StructureViewTreeElement, sb, "$indent  ")
        }
    }

    fun getOpeningBlockAt(psiFile: PsiFile, editRange: TextRange): Block? {
        val blocks = mutableListOf<Block>()
        ReadAction.compute(ThrowableComputable {
            val settings = CodeStyleSettings.getDefaults()
            val modelBuilder = LanguageFormatting.INSTANCE.forContext(psiFile)
            val formattingModel = modelBuilder?.createModel(FormattingContext.create(psiFile, settings))
            val rootBlock = formattingModel?.rootBlock

            rootBlock?.let { collectBlocks(it, blocks, editRange) }
        })

        val docLength = psiFile.textLength
        blocks.removeAll { it.textRange.length == docLength }
        blocks.removeAll { it::class.java.simpleName == "LeafBlock" }
        blocks.removeAll { it.textRange.length == 1 }
        return blocks.filter { it.textRange.startOffset >= editRange.startOffset }.firstOrNull()
    }

    private fun collectBlocks(block: Block, blocks: MutableList<Block>, range: TextRange) {
        if (block.textRange.intersects(range)) {
            blocks.add(block)
            block.subBlocks.forEach { subBlock -> collectBlocks(subBlock, blocks, range) }
        }
    }
}
