package dev.voqal.assistant.tool.custom

import com.intellij.openapi.components.service
import com.profesorfalken.jpowershell.PowerShell
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonObject
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import java.util.concurrent.TimeUnit

abstract class ExecTool(
    override val name: String,
    val command: String,
    val type: String? = null
) : VoqalTool() {

    companion object {
        fun templateString(input: String, variables: Map<String, String>): String {
            val regexPattern = """\$\[\[\s*(\w+)\s*\]\]""".toRegex()
            return regexPattern.replace(input) { matchResult ->
                val key = matchResult.groups[1]?.value ?: return@replace matchResult.value
                variables[key] ?: matchResult.value
            }
        }
    }

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        val log = project.getVoqalLogger(this::class)
        //add voqal args
        args.put("project_home", project.basePath?.let { File(it).absolutePath } ?: "")
        val runCommand = templateString(this.command, args.map as Map<String, String>)

        project.service<VoqalStatusService>().updateText("Executing tool: $name")
        log.debug("Executing command: $runCommand")
        try {
            if (type == "powershell") { //todo: configurable maxWait
                val inputString = StringReader(runCommand)
                val reader = BufferedReader(inputString)
                val resp = PowerShell.openSession().configuration(mapOf("maxWait" to Integer.MAX_VALUE.toString()))
                    .executeScript(reader)
                log.debug("Power shell output: " + resp.commandOutput)
            } else {
                runCommand.runCommand()
                log.debug("Command executed successfully")
            }
        } catch (e: Throwable) {
            log.warn("Command failed: $runCommand", e)
        }
        project.service<VoqalStatusService>().updateText("Finished executing tool: $name")
    }

    private fun String.runCommand(workingDir: File? = null) {
        val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        if (!process.waitFor(1, TimeUnit.HOURS)) {
            process.destroy()
            throw RuntimeException("execution timed out: $this")
        }
        if (process.exitValue() != 0) {
            throw RuntimeException("execution failed with code ${process.exitValue()}: $this")
        }
    }

    override fun asTool(directive: VoqalDirective) = throw UnsupportedOperationException("Not supported")
}
