package dev.voqal.assistant.template

import com.intellij.formatting.*
import com.intellij.lang.LanguageFormatting
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.util.ui.JBUI
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.getVoqalLogger
import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate
import io.vertx.core.json.JsonObject
import kotlinx.serialization.json.Json

class ChunkTextExtension : AbstractExtension() {

    companion object {
        fun setVisibleRangeHighlighter(project: Project, editor: Editor, editRange: ProperTextRange) {
            val textAttributes = TextAttributes()
            textAttributes.backgroundColor = JBUI.CurrentTheme.ToolWindow.background()
            val highlighter = editor.markupModel.addRangeHighlighter(
                editRange.startOffset, editRange.endOffset,
                HighlighterLayer.SELECTION,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
            )
            project.service<VoqalMemoryService>().putUserData("visibleRangeHighlighter", highlighter)
            project.getVoqalLogger(this::class).debug("Highlighted visible range: $editRange")
        }
    }

    override fun getFunctions() = mapOf(
        "chunkText" to ChunkTextFunction()
    )

    class ChunkTextFunction : Function {

        override fun getArgumentNames(): List<String> {
            return listOf("viewingCode", "limit", "limitType")
        }

        override fun execute(
            args: Map<String, Any?>,
            self: PebbleTemplate,
            context: EvaluationContext,
            lineNumber: Int
        ): Any? {
            val directive = context.getVariable("directive") as? VoqalDirective
            val editor = directive?.ide?.editor
            if (editor == null) {
                return args["viewingCode"]
            } else if (args["viewingCode"] == null) {
                return null
            } else if (System.getenv("VQL_BENCHMARK_MODE") == "true") {
                return args["viewingCode"] //todo: this
            }

            val log = directive.project.getVoqalLogger(this::class)
            val visibleText: String?
            val memoryService = directive.project.service<VoqalMemoryService>()
            var editRange = memoryService.getUserData("visibleRange") as? ProperTextRange
            if (editRange == null) {
                var initialVisibleRange: ProperTextRange? = null
                ApplicationManager.getApplication().invokeAndWait {
                    initialVisibleRange = editor.calculateVisibleRange()
                }
                editRange = initialVisibleRange!!
                log.debug("Initial visible range: $editRange")

                val limit = args["limit"]?.toString()?.toInt() ?: 0
                val limitType = args["limitType"]

                val file = directive.developer.viewingFile
                if (file != null && limitType == "LINES") {
                    val originalEditRange = editRange

                    if (args["limitType"] == "LINES") {
                        val limit = args["limit"].toString().toInt() - 1
                        val startLine = editor.document.getLineNumber(editRange.startOffset)
                        val endLine = editor.document.getLineNumber(editRange.endOffset)
                        val visibleLines = endLine - startLine
                        val documentLineCount = editor.document.lineCount

                        if (visibleLines < limit && documentLineCount > 0) {
                            var newStartLine = startLine
                            var newEndLine = endLine
                            var linesToAdjust = Math.abs(visibleLines - limit)

                            while (linesToAdjust > 0) {
                                // Expand equally from top and bottom
                                if (newStartLine > 0) {
                                    newStartLine--
                                    linesToAdjust--
                                }
                                if (linesToAdjust > 0 && newEndLine < documentLineCount - 1) {
                                    newEndLine++
                                    linesToAdjust--
                                }
                                if (newStartLine == 0 && newEndLine == documentLineCount - 1) {
                                    break // Stop expanding if both boundaries are hit
                                }
                            }

                            val topOffset = editor.document.getLineStartOffset(newStartLine)
                            val bottomOffset = editor.document.getLineEndOffset(newEndLine)
                            editRange = ProperTextRange(topOffset, bottomOffset)
                        }
                    } else if (args["limitType"] != null) {
                        throw IllegalArgumentException("Unsupported limit type: ${args["limitType"]}")
                    }

                    val psiFile = ReadAction.compute(ThrowableComputable {
                        PsiManager.getInstance(directive.project).findFile(file)!!
                    })
                    val smartEditRange = smartChunk(editor, psiFile, limit, editRange, initialVisibleRange!!)

                    if (smartEditRange != editRange) {
                        editRange = smartEditRange
                        log.debug("Smart code chunked code from $originalEditRange to $editRange")
                    } else if (smartEditRange != editRange) {
                        log.debug("Smart code chunking failed, falling back to initial visible range")
                        editRange = initialVisibleRange!!
                    }

                    memoryService.putUserData("visibleRange", editRange)
                }

                //paint visible range
                if (editRange.length != editor.document.textLength) {
                    val existingHighlighter = memoryService
                        .getUserData("visibleRangeHighlighter") as? RangeHighlighter
                    if (existingHighlighter == null) {
                        setVisibleRangeHighlighter(directive.project, editor, editRange)
                    }
                }
            } else if (memoryService.getUserData("voqal.edit.inlay") == null) {
                //if full code visible but current document is less than edit range, reset edit range
                val existingHighlighter = directive.project.service<VoqalMemoryService>()
                    .getUserData("visibleRangeHighlighter") as? RangeHighlighter
                if (existingHighlighter == null && editRange.length > editor.document.text.length) {
                    editRange = ProperTextRange(0, editor.document.textLength)
                    directive.project.service<VoqalMemoryService>()
                        .putUserData("visibleRange", editRange)
                    log.debug("Reset edit range to full code: $editRange")
                } //todo: this doesn't handle cases where the code is too long to fit in the editor
                //todo: this may not be needed since the visibleText is only sent on initial message to LLM
                //todo: could probably just cache visibleText
            }

            visibleText = editRange.substring(editor.document.text)
            val viewingCode = ViewingCode(JsonObject.mapFrom(args["viewingCode"]))
            val startLine = editor.document.getLineNumber(editRange.startOffset)
            val finalViewingCode = viewingCode.copy(
                code = visibleText,
                codeWithLineNumbers = visibleText.split("\n").mapIndexed { index, line ->
                    "${startLine + index + 1}|$line"
                }.joinToString("\n")
            )
            return VoqalDirectiveService.convertJsonElementToMap(
                Json.parseToJsonElement(finalViewingCode.toJson().toString())
            )
        }

        private fun smartChunk(
            editor: Editor,
            psiFile: PsiFile,
            maxLines: Int,
            editRange: TextRange,
            visibleRange: TextRange
        ): ProperTextRange {
            val document = editor.document

            // Get the blocks within the initial visible range
            val blocks = mutableListOf<Block>()
            val rootBlock = ReadAction.compute(ThrowableComputable {
                val settings = CodeStyleSettings.getDefaults()
                val modelBuilder = LanguageFormatting.INSTANCE.forContext(psiFile)
                val formattingModel = modelBuilder?.createModel(FormattingContext.create(psiFile, settings))
                val rootBlock = formattingModel?.rootBlock

                rootBlock?.let { collectBlocks(it, blocks, editRange) }
                rootBlock
            })
            if (rootBlock == null) {
                return ProperTextRange(editRange.startOffset, editRange.endOffset)
            }

            // Remove unnecessary stuff
            blocks.removeAll { it::class.java.simpleName == "LeafBlock" }
            blocks.removeAll { it.textRange.length == 1 }
            blocks.removeAll { document.getText(it.textRange).lines().size > (maxLines + 2) } //+ 2 for SynthBlock

            // Add synthetic blocks with removed starting/ending brackets
            blocks.toList().forEach { block ->
                val text = document.getText(block.textRange)
                val lines = text.lines()
                if (lines.size > 1) {
                    val firstLine = lines.first()
                    val lastLine = lines.last()
                    if (firstLine == "{" && lastLine == "}") {
                        val startOffset = block.textRange.startOffset + 2
                        val endOffset = block.textRange.endOffset - 2
                        if (startOffset < endOffset) {
                            blocks.add(SynthBlock(startOffset, endOffset))
                        }
                    }
                }
            }

            // Sort blocks by their size in descending order
            blocks.sortByDescending { block ->
                val lines = document.getText(block.textRange).lines()
                lines.size
            }

            // Start with the largest block under the line limit that intersects the visible range
            val selectedBlock = blocks.firstOrNull { block ->
                val lines = document.getText(block.textRange).lines()
                lines.size <= maxLines && block.textRange.intersects(visibleRange)
            } ?: return ProperTextRange(editRange.startOffset, editRange.endOffset)
            blocks.remove(selectedBlock)

            // Remove everything that's the parent of the selected block
            blocks.removeAll { isAncestor(selectedBlock, it) }

            // Remove everything outside editing range
            blocks.toList().forEach { block ->
                if (!editRange.contains(block.textRange)) {
                    blocks.remove(block)
                }
            }

            val selectedBlocks = mutableListOf<Block>()
            var cumulativeLineCount = document.getText(selectedBlock.textRange).lines().size
            selectedBlocks.add(selectedBlock)

            val commonParent = getBlockParent(rootBlock, selectedBlock) ?: rootBlock
            val rejectedBlocks = mutableListOf<Block>()
            // Auto-reject blocks before/after the start/end lines of the common parent
            val commonParentStartLine = document.getLineNumber(commonParent.textRange.startOffset)
            val commonParentEndLine = document.getLineNumber(commonParent.textRange.endOffset)
            blocks.toList().forEach { block ->
                val blockStartLine = document.getLineNumber(block.textRange.startOffset)
                val blockEndLine = document.getLineNumber(block.textRange.endOffset)
                if (blockStartLine <= commonParentStartLine || blockEndLine >= commonParentEndLine) {
                    rejectedBlocks.add(block)
                }
            }
            blocks.removeAll(rejectedBlocks)

            // Add on any contiguous blocks
            for (block in blocks) {
                val isSibling = ReadAction.compute(ThrowableComputable {
                    commonParent.subBlocks.contains(block)
                })
                if (!isSibling) {
                    continue
                }

                val blockSize = document.getText(block.textRange).lines().size
                if (!selectedBlocks.contains(block) && cumulativeLineCount + blockSize <= maxLines) {
                    val isChildOfRejected = ReadAction.compute(ThrowableComputable {
                        rejectedBlocks.any { isAncestor(it, block) }
                    })

                    if (!isChildOfRejected) {
                        val minStartOffset = selectedBlocks.minOf { it.textRange.startOffset }
                        val maxEndOffset = selectedBlocks.maxOf { it.textRange.endOffset }
                        val minStartLine = document.getLineNumber(minStartOffset)
                        val minStartLineOffset = document.getLineStartOffset(minStartLine)
                        val maxEndLine = document.getLineNumber(maxEndOffset)
                        val maxEndLineOffset = document.getLineEndOffset(maxEndLine)
                        val proposedRange = TextRange(
                            Math.min(minStartLineOffset, block.textRange.startOffset),
                            Math.max(maxEndLineOffset, block.textRange.endOffset)
                        )
                        val newLineCount = document.getText(proposedRange).lines().size
                        if (newLineCount <= maxLines) {
                            selectedBlocks.add(block)
                            cumulativeLineCount += document.getText(block.textRange).lines().size
                        } else {
                            rejectedBlocks.add(block)
                        }
                    } else {
                        rejectedBlocks.add(block)
                    }
                } else {
                    val isChildOfSelectedBlock = ReadAction.compute(ThrowableComputable {
                        selectedBlocks.any { isAncestor(it, block) }
                    })
                    if (!isChildOfSelectedBlock) {
                        rejectedBlocks.add(block)
                    }
                }
            }
            selectedBlocks.sortBy { it.textRange.startOffset }

            val firstBlock = selectedBlocks.first()
            val lastBlock = selectedBlocks.last()
            val lineStart = document.getLineNumber(firstBlock.textRange.startOffset)
            val lineEnd = document.getLineNumber(lastBlock.textRange.endOffset)
            return ProperTextRange(
                document.getLineStartOffset(lineStart),
                document.getLineEndOffset(lineEnd)
            )
        }

        /**
         * Check if block1 is a parent of block2 or a parent of block2's parent.
         */
        private fun isAncestor(block1: Block, block2: Block): Boolean {
            if (block1 === block2) {
                return true
            }
            for (subBlock in block1.subBlocks) {
                if (containsBlock(block2, subBlock)) {
                    return true
                }
            }
            return false
        }

        private fun containsBlock(target: Block, current: Block): Boolean {
            if (current === target) {
                return true
            }
            for (subBlock in current.subBlocks) {
                if (containsBlock(target, subBlock)) {
                    return true
                }
            }
            return false
        }

        private fun collectBlocks(block: Block, blocks: MutableList<Block>, range: TextRange) {
            if (block.textRange.intersects(range)) {
                blocks.add(block)
                block.subBlocks.forEach { subBlock -> collectBlocks(subBlock, blocks, range) }
            }
        }

        private fun getBlockParent(rootBlock: Block, block: Block): Block? {
            if (rootBlock.subBlocks.contains(block)) {
                return rootBlock
            }
            for (subBlock in rootBlock.subBlocks) {
                val parent = getBlockParent(subBlock, block)
                if (parent != null) {
                    return parent
                }
            }
            return null
        }

        private class SynthBlock(val startOffset: Int, val endOffset: Int) : Block {
            override fun getTextRange(): TextRange = TextRange(startOffset, endOffset)
            override fun getSubBlocks(): List<Block?> = emptyList()
            override fun getWrap(): Wrap? = null
            override fun getIndent(): Indent? = null
            override fun getAlignment(): Alignment? = null
            override fun getSpacing(
                child1: Block?,
                child2: Block
            ): Spacing? = null

            override fun getChildAttributes(newChildIndex: Int): ChildAttributes =
                ChildAttributes(null, null)

            override fun isIncomplete(): Boolean = false
            override fun isLeaf(): Boolean = true
        }
    }
}
