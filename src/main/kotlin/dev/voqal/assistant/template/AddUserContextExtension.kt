package dev.voqal.assistant.template

import com.intellij.openapi.components.service
import dev.voqal.assistant.VoqalDirective
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.getVoqalLogger
import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

class AddUserContextExtension : AbstractExtension() {

    override fun getFunctions() = mapOf(
        "addUserContext" to AddContextFunction()
    )

    class AddContextFunction : Function {
        override fun getArgumentNames(): List<String> {
            return listOf("key", "value")
        }

        override fun execute(
            args: Map<String, Any?>,
            self: PebbleTemplate,
            context: EvaluationContext,
            lineNumber: Int
        ): Any? {
            val project = (context.getVariable("directive") as? VoqalDirective)?.project ?: return null
            val log = project.getVoqalLogger(this::class)
            val key = args["key"] as? String
            if (key == null) {
                log.warn("Key is null")
                return null

            }
            val value = args["value"]

            val memoryService = project.service<VoqalMemoryService>()
            memoryService.putLongTermUserData(key, value)

            return null
        }
    }
}
