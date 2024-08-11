package dev.voqal.assistant.tool.ide

import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.core.Parameters
import com.intellij.execution.ui.RunContentManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.tool.VoqalTool
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.invokeLater
import io.vertx.core.Promise
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait

class ShowTabTool : VoqalTool() {

    companion object {
        const val NAME = "show_tab"
    }

    override val name = NAME

    override suspend fun actionPerformed(args: JsonObject, directive: VoqalDirective) {
        val project = directive.ide.project
        focusOnSpecificTab(project, args.getString("tab_name"))
        project.service<VoqalStatusService>().updateText("Tool $name")
    }

    private suspend fun focusOnSpecificTab(project: Project, tabTitle: String) {
        val log = project.getVoqalLogger(this::class)
        log.info("Attempting to focus on tab: $tabTitle")
        val runContentManager = RunContentManager.getInstance(project)
        val currentDescriptor = runContentManager.selectedContent

        //todo: better
        val tabTitle = if (tabTitle.equals("threads", true)) {
            "Variables"
        } else if (tabTitle.equals("threads and variables", true)) {
            "Variables"
        } else tabTitle
        log.info("Using tab title: $tabTitle")

        val promise = Promise.promise<Void>()
        val layoutUi = currentDescriptor?.runnerLayoutUi
        if (layoutUi != null) {
            var tabShown = false
            for (content in layoutUi.contents) {
                if (content.displayName.equals(tabTitle, true)) {
                    log.info("Showing tab: " + content.displayName)
                    project.invokeLater {
                        layoutUi.selectAndFocus(content, true, true)
                    }
                    tabShown = true
                    promise.complete()
                    break
                }
            }
            if (!tabShown) {
                log.warn("Couldn't find tab: $tabTitle")
                promise.complete()
            }
        } else {
            log.warn("Couldn't find layoutUi")
            promise.complete()
        }
        promise.future().coAwait()
    }

    override fun asTool(directive: VoqalDirective) = Tool.function(
        name = NAME,
        description = "Show/focus on a specific visible tab.",
        parameters = Parameters.fromJsonString(JsonObject().apply {
            put("type", "object")
            put("properties", JsonObject().apply {
                put("tab_name", JsonObject().apply { //todo: enum of available tabs
                    put("type", "string")
                    put("description", "The name of the tab to show.")
                })
            })
            put("required", JsonArray().add("tab_name"))
        }.toString())
    )
}
