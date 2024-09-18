package dev.voqal.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.descendants
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

fun PsiElement.isFunction(): Boolean {
    val psiElement = this
    return psiElement::class.java.simpleName.startsWith("KtNamedFunction")
            || psiElement::class.java.simpleName.startsWith("PsiMethodImpl")
            || psiElement::class.java.simpleName.startsWith("PyFunction")
            || psiElement::class.java.simpleName.startsWith("GoFunctionDeclaration")
            || psiElement::class.java.simpleName.startsWith("GoMethodDeclaration")
            || psiElement::class.java.simpleName.startsWith("JSFunctionImpl")
}

fun PsiElement.isCodeBlock(): Boolean {
    val psiElement = this
    return psiElement::class.java.simpleName.startsWith("KtBlockExpression")
            || psiElement::class.java.simpleName.startsWith("PsiCodeBlockImpl")
            || psiElement::class.java.simpleName.startsWith("PyStatementList")
            || psiElement::class.java.simpleName.startsWith("GoBlockImpl")
            || psiElement::class.java.simpleName.startsWith("JSBlockStatementImpl")
}

fun PsiElement.isField(): Boolean {
    val psiElement = this
    return psiElement::class.java.simpleName.startsWith("KtProperty")
            || psiElement::class.java.simpleName.startsWith("PsiFieldImpl")
}

fun PsiElement.isClass(): Boolean {
    val psiElement = this
    return psiElement::class.java.simpleName.startsWith("KtClass")
            || psiElement::class.java.simpleName.startsWith("PsiClass")
            || psiElement::class.java.simpleName.startsWith("PyClass")
            || psiElement::class.java.simpleName.startsWith("GoTypeSpecImpl")
            || psiElement::class.java.simpleName.startsWith("ES6ClassImpl")
}

fun PsiElement.isFile(): Boolean {
    val psiElement = this
    return psiElement::class.java.simpleName.startsWith("KtFile")
            || psiElement::class.java.simpleName.startsWith("PsiJavaFile")
            || psiElement::class.java.simpleName.startsWith("PyFile")
            || psiElement::class.java.simpleName.startsWith("GoFile")
            || psiElement::class.java.simpleName.startsWith("JSFileImpl")
}

fun PsiElement.isIdentifier(): Boolean {
    val psiElement = this
    return psiElement.toString().contains("PsiIdentifier")
            || psiElement.toString().contains("IDENTIFIER")
}

fun PsiElement.isJvm(): Boolean {
    return this.language.id.lowercase() == "java" || this.language.id.lowercase() == "kotlin"
}

fun PsiElement.isPython(): Boolean {
    return this.language.id.lowercase() == "python"
}

fun PsiElement.isGo(): Boolean {
    return this.language.id.lowercase() == "go"
}

fun PsiElement.getCodeBlock(): PsiElement {
    return descendants()
        .filter { it.isCodeBlock() }
        .first()
}

fun PsiFile.getFunctions(): List<PsiNamedElement> {
    return descendants()
        .filter { it is PsiNamedElement }
        .filter { it.isFunction() }
        .map { it as PsiNamedElement }.toList()
}

fun PsiFile.getFields(): List<PsiNamedElement> {
    return descendants()
        .filter { it is PsiNamedElement }
        .filter { it.isField() }
        .map { it as PsiNamedElement }.toList()
}

fun PsiFile.getClasses(): List<PsiNamedElement> {
    return descendants()
        .filter { it is PsiNamedElement }
        .filter { it.isClass() }
        .map { it as PsiNamedElement }.toList()
}

val RangeMarker.range: TextRange?
    get() {
        if (!isValid) {
            return null
        } else {
            val start = startOffset
            val end = endOffset
            return if ((if (0 <= start) start <= end else false)) TextRange(start, end) else null
        }
    }
