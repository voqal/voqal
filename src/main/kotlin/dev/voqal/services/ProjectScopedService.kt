package dev.voqal.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.messages.MessageBusConnection
import dev.voqal.ide.logging.LoggerFactory
import dev.voqal.ide.logging.LoggerFactory.VoqalLogger
import dev.voqal.ide.ui.toolwindow.tab.VoqalLogsTab
import dev.voqal.utils.SharedAudioCapture
import kotlinx.coroutines.*
import kotlin.reflect.KClass

@Service(Service.Level.PROJECT)
class ProjectScopedService(project: Project) : Disposable {

    private val log = project.getVoqalLogger(this::class)
    internal val messageBusConnection = project.messageBus.connect()
    internal val audioCapture by lazy { SharedAudioCapture(project) }
    internal val voqalLogsTab by lazy { VoqalLogsTab(project) }

    private val handler = CoroutineExceptionHandler { _, exception ->
        log.errorChat("Unhandled exception: ${exception.message}", exception)
    }
    private val job = Job().apply {
        invokeOnCompletion { cause ->
            if (cause != null && !project.isDisposed) {
                log.error("Voqal plugin failure. Please restart your IDE.", cause)
            }
        }
    }
    internal val scope = CoroutineScope(Dispatchers.Default + job + handler)

    override fun dispose() {
        audioCapture.cancel()
        Disposer.dispose(messageBusConnection)
        scope.cancel()
    }
}

fun Project.getVoqalLogger(clazz: KClass<*>): VoqalLogger {
    return LoggerFactory.getLogger(this, clazz.java)
}

val Project.messageBusConnection: MessageBusConnection
    get() = service<ProjectScopedService>().messageBusConnection
val Project.scope: CoroutineScope
    get() = service<ProjectScopedService>().scope
val Project.logsTab: VoqalLogsTab
    get() = service<ProjectScopedService>().voqalLogsTab
val Project.audioCapture: SharedAudioCapture
    get() = service<ProjectScopedService>().audioCapture

fun Project.invokeLater(action: () -> Unit) {
    ApplicationManager.getApplication().invokeLater({
        action()
    }, disposed)
}
