package dev.voqal

import com.intellij.ide.IdeEventQueue
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.WindowManager
import dev.voqal.config.settings.PromptLibrarySettings
import dev.voqal.ide.VoqalModeCaret
import dev.voqal.services.*
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.SwingUtilities

/**
 * Initializes Voqal plugin on project open.
 */
class VoqalProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val log = project.getVoqalLogger(this::class)
        val pluginVersion = PluginManagerCore.getPlugin(PluginId.getId("dev.voqal"))!!.version
        log.info("Voqal initialized. Version: $pluginVersion")

        val configService = project.service<VoqalConfigService>()
        var pluginEnabled = configService.getConfig().pluginSettings.enabled
        var pauseOnFocusLost = configService.getConfig().pluginSettings.pauseOnFocusLost
        configService.onConfigChange(project.service<ProjectScopedService>()) {
            pluginEnabled = it.pluginSettings.enabled
            if (pauseOnFocusLost != it.pluginSettings.pauseOnFocusLost) {
                pauseOnFocusLost = it.pluginSettings.pauseOnFocusLost
                if (!pauseOnFocusLost) {
                    project.audioCapture.resume() //make sure resumed
                }
            }
        }
        val projectFrame = WindowManager.getInstance().getIdeFrame(project)
        if (projectFrame == null) {
            log.warn("Unable to get project frame")
        }
        projectFrame?.let {
            val component = projectFrame.component
            val window = SwingUtilities.windowForComponent(component)
            if (window == null) {
                log.warn("Unable to get window for project frame")
            }
            window?.addWindowFocusListener(object : WindowFocusListener {
                override fun windowGainedFocus(e: WindowEvent?) {
                    if (pluginEnabled && pauseOnFocusLost && !project.isDisposed) {
                        log.trace("IDE gained focus")
                        project.audioCapture.resume()
                    }
                }

                override fun windowLostFocus(e: WindowEvent?) {
                    if (pluginEnabled && pauseOnFocusLost && !project.isDisposed) {
                        log.trace("IDE lost focus")
                        project.audioCapture.pause()
                    }
                }
            })
        }

        //ensure prompt library is complete
        val promptLibrary = configService.getConfig().promptLibrarySettings
        val missingPrompts = PromptLibrarySettings.DEFAULT_PROMPTS.filter {
            it.promptName !in promptLibrary.prompts.map { it.promptName }
        }
        if (missingPrompts.isNotEmpty()) {
            val allPrompts = (promptLibrary.prompts + missingPrompts).sortedBy { it.promptName }
            configService.updateConfig(promptLibrary.copy(prompts = allPrompts))
            log.info("Added prompts to prompt library: ${missingPrompts.joinToString { it.promptName }}")
        }

        //make sure no dupes in prompt library
        val promptLibrarySettings = configService.getConfig().promptLibrarySettings
        val promptLibraryPrompts = promptLibrarySettings.prompts
        val uniquePrompts = promptLibraryPrompts.distinctBy { it.promptName }
        if (promptLibraryPrompts.size != uniquePrompts.size) {
            configService.updateConfig(promptLibrarySettings.copy(prompts = uniquePrompts))
            log.info("Removed duplicate prompts from prompt library")
        }

        //start plugin
        project.service<VoqalStatusService>()

        //add voqal mode caret to all existing editors
        val voqalCarets = mutableMapOf<Editor, VoqalModeCaret>()
        val editors = EditorFactory.getInstance().allEditors
        for (editor in editors) {
            if (voqalCarets[editor] == null && editor.project == project) {
                val caret = VoqalModeCaret(project, editor)
                voqalCarets[editor] = caret
                Disposer.register(project.service<ProjectScopedService>(), caret)
            }
        }
        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
            override fun editorCreated(event: EditorFactoryEvent) {
                val editor = event.editor
                if (voqalCarets[editor] == null && editor.project == project) {
                    val caret = VoqalModeCaret(project, editor)
                    Disposer.register(project.service<ProjectScopedService>(), caret)
                    voqalCarets[editor] = caret
                }
            }

            override fun editorReleased(event: EditorFactoryEvent) {
                val editor = event.editor
                val caret = voqalCarets[editor]
                if (caret != null) {
                    Disposer.dispose(caret)
                    voqalCarets.remove(editor)
                }
            }
        }, project)
        IdeEventQueue.getInstance().addDispatcher({ event ->
            if (event is KeyEvent) {
                voqalCarets.forEach { (_, voqalCaret) -> voqalCaret.repaint() }
            }
            false
        }, project)

        //pre-initialize AI provider
        if (!ApplicationManager.getApplication().isUnitTestMode) {
            project.service<VoqalConfigService>().getAiProvider()
        }
    }
}
