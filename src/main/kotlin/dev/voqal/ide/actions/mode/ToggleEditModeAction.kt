package dev.voqal.ide.actions.mode

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import dev.voqal.assistant.tool.system.mode.ToggleEditModeTool
import dev.voqal.services.VoqalToolService
import dev.voqal.services.getVoqalLogger
import dev.voqal.services.scope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock

class ToggleEditModeAction : AnAction("Edit Mode") {

    private val syncLock = ReentrantLock()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val log = project.getVoqalLogger(this::class)
        log.debug("Toggle edit mode action triggered")

        project.scope.launch {
            if (syncLock.tryLock()) {
                try {
                    runBlocking {
                        project.service<VoqalToolService>().blindExecute(ToggleEditModeTool())
                    }
                } finally {
                    syncLock.unlock()
                }
            } else {
                log.debug("Ignoring toggle edit mode action")
            }
        }
    }
}
