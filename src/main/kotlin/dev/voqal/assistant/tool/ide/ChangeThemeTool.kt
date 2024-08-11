package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfoImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.joor.Reflect

class ChangeThemeTool : VoqalTool() {

    companion object {
        const val NAME = "change_theme"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.project
        val log = project.getVoqalLogger(this::class)
        val theme = args.getString("theme")
        if (theme == null) {
            log.warn("No theme found")
            project.service<VoqalStatusService>().updateText("No theme found")
            return
        }

        val lafManager = LafManager.getInstance()
        val themeManagerClass = Class.forName("com.intellij.ide.ui.laf.UiThemeProviderListManager")
        val reqTheme = Reflect.on(ApplicationManager.getApplication().getService(themeManagerClass))
            .call("getLaFs").get<Sequence<Any>>().toList().find {
                it is UIThemeLookAndFeelInfoImpl && it.name.lowercase() == theme.lowercase()
            }
        if (reqTheme == null) {
            log.warn("Theme not found: $theme")
            project.service<VoqalStatusService>().updateText("Theme not found: $theme")
            return
        }

        log.info("Changing theme to: $theme")
        ApplicationManager.getApplication().invokeAndWait {
            Reflect.onClass("com.intellij.ide.actions.QuickChangeLookAndFeel")
                .call("switchLafAndUpdateUI", lafManager, reqTheme, false)
        }
        project.service<VoqalStatusService>().updateText("Changed theme to: $theme")
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Change the theme of the IDE.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("theme", JsonObject().apply {
                    put("type", "string")
                    put("description", "The theme to change to.")
                    put("enum", JsonArray().add("light").add("dark"))
                })
            })
            put("required", JsonArray().add("theme"))
        }.toString())
    )
}
