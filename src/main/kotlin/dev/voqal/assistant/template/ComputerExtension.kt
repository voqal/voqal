package dev.voqal.assistant.template

import io.pebbletemplates.pebble.extension.AbstractExtension
import java.util.*

class ComputerExtension : AbstractExtension() {
    override fun getGlobalVariables(): Map<String, Any> {
        return mapOf(
            "computer" to mapOf(
                "currentTime" to Date(),
                "osName" to System.getProperty("os.name"),
                "osVersion" to System.getProperty("os.version"),
                "osArch" to System.getProperty("os.arch")
            )
        )
    }
}
