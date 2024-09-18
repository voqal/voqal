package benchmark.suites.idle

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import com.aallam.openai.api.chat.ToolCall
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.util.ThrowableComputable
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.tool.ide.navigation.GotoLineTool
import dev.voqal.assistant.tool.ide.navigation.GotoTextTool
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.getFunctions
import io.vertx.core.json.JsonObject

/**
 * A suite to test the goto tools.
 */
class GotoSuite : BenchmarkSuite {

    fun `ignoring the constructor jump to the third function`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "AdventureGame"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        //will either goto by text
        val expectedGoto = mutableListOf("showStatus", "show_status")

        //or by line
        val expectedLines = ReadAction.compute(ThrowableComputable {
            val thirdFunction = psiFile.getFunctions()[2]
            val document = psiFile.viewProvider.document
            val startLine = document.getLineNumber(thirdFunction.textRange.startOffset) + 1
            val endLine = document.getLineNumber(thirdFunction.textRange.endOffset) + 1
            Pair(startLine, endLine)
        })
        for (i in expectedLines.first until expectedLines.second) {
            expectedGoto.add("$i")
        }

        command.verifyGoto(expectedGoto)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    fun `show me the simplest function`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "AdventureGame"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        //will either goto by text
        val expectedGoto = mutableListOf("findNothing", "find_nothing")

        //or by line
        val expectedLines = ReadAction.compute(ThrowableComputable {
            val function = psiFile.getFunctions().find { expectedGoto.contains(it.name) }!!
            val document = psiFile.viewProvider.document
            val startLine = document.getLineNumber(function.textRange.startOffset) + 1
            val endLine = document.getLineNumber(function.textRange.endOffset) + 1
            Pair(startLine, endLine)
        })
        for (i in expectedLines.first until expectedLines.second) {
            expectedGoto.add("$i")
        }

        command.verifyGoto(expectedGoto)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    private fun BenchmarkPromise.verifyGoto(expectedGoto: List<String>) {
        this.promise.future().onSuccess {
            val toolCalls = response?.toolCalls?.map { (it as ToolCall.Function).function } ?: emptyList()
            if (toolCalls.size == 1 && toolCalls.first().name.startsWith("goto")) {
                it.success("Expected tool(s) used")
            } else {
                it.fail("Unexpected tool(s) used: ${toolCalls.map { it.name }}")
            }

            val gotoTool = toolCalls.firstOrNull { it.name.startsWith("goto") }
            if (gotoTool != null) {
                if (gotoTool.name == GotoTextTool.NAME) {
                    checkTextContains(expectedGoto, JsonObject(gotoTool.arguments).getString("text"), this)
                } else if (gotoTool.name == GotoLineTool.NAME) {
                    val lineNumber = JsonObject(gotoTool.arguments).getString("lineNumber")
                    if (expectedGoto.contains(lineNumber)) {
                        it.success("Expected line number used")
                    } else {
                        it.fail("Unexpected line number used: $lineNumber")
                    }
                } else {
                    it.fail("Unexpected goto tool: ${gotoTool.name}")
                }
            } else {
                it.fail("No goto tool found")
            }

            it.testFinished()
        }
    }
}
