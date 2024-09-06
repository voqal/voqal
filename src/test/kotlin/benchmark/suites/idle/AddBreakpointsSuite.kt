package benchmark.suites.idle

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import com.aallam.openai.api.chat.ToolCall
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.assistant.tool.ide.AddBreakpointsTool
import dev.voqal.config.settings.PromptSettings

/**
 * A suite to test the addition of breakpoints.
 */
class AddBreakpointsSuite : BenchmarkSuite {

    /**
     * Add breakpoints to the two return statements.
     */
    fun `add breakpoints to the return statements`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "RemoveMethod"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        val expectedLines = mutableListOf<Int>()
        code.split("\n").forEachIndexed { index, line ->
            if (line.contains("return")) {
                expectedLines.add(index)
            }
        }

        command.verifyBreakpoints(virtualFile, expectedLines)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    private fun BenchmarkPromise.verifyBreakpoints(virtualFile: VirtualFile, expectedLines: List<Int>) {
        this.promise.future().onSuccess {
            val breakpointManager = XDebuggerManager.getInstance(project).breakpointManager
            val breakpoints = breakpointManager.allBreakpoints
                .filterIsInstance<XLineBreakpoint<*>>()
                .filter { it.fileUrl == virtualFile.url }
                .sortedBy { it.line }

            if (breakpoints.size != expectedLines.size) {
                it.fail("Expected ${expectedLines.size} breakpoints, found ${breakpoints.size}")
            } else {
                it.success("Found ${expectedLines.size} breakpoint(s)")
            }
            expectedLines.forEach { line ->
                val breakpoint = breakpoints.find { it.line == line }
                if (breakpoint == null) {
                    it.fail("Breakpoint not found on line ${line + 1}")
                } else {
                    it.success("Breakpoint found on line ${line + 1}")
                }
            }

            //ensure only add breakpoints tool used
            val toolCalls = response?.toolCalls?.map { (it as ToolCall.Function).function.name } ?: emptyList()
            if (toolCalls.isNotEmpty() && toolCalls.all { it == AddBreakpointsTool.NAME }) {
                it.success("Expected tool(s) used")
            } else {
                val unexpectedToolCalls = toolCalls.filter { it != AddBreakpointsTool.NAME }
                it.fail("Unexpected tool(s) used: $unexpectedToolCalls")
            }

            it.testFinished()
        }
    }
}
