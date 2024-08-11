package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class RemoveBreakpointsTool : VoqalTool() {

    companion object {
        const val NAME = "remove_breakpoints"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        val lineNumbers = args.getJsonArray("line_numbers").map { it.toString().toInt() - 1 }
        if (lineNumbers.isEmpty()) {
            log.warn("No line numbers provided to remove breakpoint from.")
            project.service<VoqalDirectiveService>().handleResponse(
                "No line numbers provided to remove breakpoint from."
            )
            return
        }
        log.info("Removing breakpoint from lines: $lineNumbers")

        val psiFile = directive.ide.editor?.let {
            ReadAction.compute(ThrowableComputable {
                PsiDocumentManager.getInstance(project).getPsiFile(it.document)
            })
        }
        val virtualFile = psiFile!!.virtualFile
        removeLineBreakpoints(project, virtualFile, lineNumbers)
        project.service<VoqalStatusService>().updateText("Tool $name")
    }

    private fun removeLineBreakpoints(project: Project, virtualFile: VirtualFile, lines: List<Int>) {
        val breakpointManager = XDebuggerManager.getInstance(project).breakpointManager
        val breakpoints = breakpointManager.allBreakpoints
            .filterIsInstance<XLineBreakpoint<*>>()
            .filter { it.fileUrl == virtualFile.url }
        breakpoints.forEach {
            if (it.line in lines) {
                breakpointManager.removeBreakpoint(it)
            }
        }
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Removes breakpoints from the specified line numbers.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("line_numbers", JsonObject().apply {
                    put("type", "array")
                    put("description", "The line number(s) to remove a breakpoint from.")
                    put("items", JsonObject().put("type", "string"))
                })
            })
            put("required", JsonArray().add("line_numbers"))
        }.toString())
    )
}
