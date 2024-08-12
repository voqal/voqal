package benchmark.model

import benchmark.model.context.ProjectFileContext
import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vcs.CodeSmellDetector
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.descendants
import com.intellij.psi.util.descendantsOfType
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.utils.vfs.getDocument
import dev.voqal.assistant.tool.code.CreateClassTool.Companion.getFileExtensionForLanguage
import dev.voqal.services.VoqalSearchService
import java.io.File

interface BenchmarkSuite {

    fun checkPsiFileErrors(
        psiFile: PsiFile,
        promise: BenchmarkPromise,
        ignoreSmells: Set<String> = emptySet()
    ) {
        var errors = 0
        psiFile.descendantsOfType<PsiErrorElement>().forEach {
            errors++
            promise.fail("Syntax error: " + it.errorDescription)
        }
        CodeSmellDetector.getInstance(psiFile.project).findCodeSmells(listOf(psiFile.virtualFile)).forEach {
            if (it.description !in ignoreSmells) {
                errors++
                promise.fail("Code smell: " + it.description)
            }
        }
        if (errors == 0) {
            promise.success("no syntax errors")
        }
    }

    fun checkTextContains(
        check: List<String>,
        text: String,
        it: BenchmarkPromise
    ) {
        if (check.any { text.contains(it) }) {
            it.success(check.toString())
        } else {
            it.fail(check.toString())
        }
    }

    fun checkTextContains(
        check: List<String>,
        virtualFile: VirtualFile,
        it: BenchmarkPromise
    ) {
        checkTextContains(check, virtualFile.getDocument().text, it)
    }

    fun checkTextContains(
        check: String,
        virtualFile: VirtualFile,
        it: BenchmarkPromise
    ) {
        checkTextContains(listOf(check), virtualFile, it)
    }

    fun checkTextContains(
        check: List<String>,
        psiElement: PsiElement,
        it: BenchmarkPromise
    ) {
        val text = psiElement.text
        if (check.any { text.contains(it) }) {
            it.success(check.toString())
        } else {
            it.fail(check.toString())
        }
    }

    fun checkTextContains(
        check: String,
        psiElement: PsiElement,
        it: BenchmarkPromise
    ) {
        checkTextContains(listOf(check), psiElement, it)
    }

    fun checkNotTextContains(
        check: String,
        psiElement: PsiElement,
        it: BenchmarkPromise
    ) {
        val text = psiElement.text
        if (!text.contains(check)) {
            it.success(check)
        } else {
            it.fail(check)
        }
    }

    fun PsiElement.getCodeBlock(): PsiElement {
        val searchService = project.service<VoqalSearchService>()
        return descendants()
            .filter { searchService.isCodeBlock(it) }
            .first()
    }

    fun PsiFile.getFunctions(): List<PsiNamedElement> {
        val searchService = project.service<VoqalSearchService>()
        return descendants()
            .filter { it is PsiNamedElement }
            .filter { searchService.isFunction(it) }
            .map { it as PsiNamedElement }.toList()
    }

    fun PsiFile.getFields(): List<PsiNamedElement> {
        val searchService = project.service<VoqalSearchService>()
        return descendants()
            .filter { it is PsiNamedElement }
            .filter { searchService.isField(it) }
            .map { it as PsiNamedElement }.toList()
    }

    fun PsiFile.getClasses(): List<PsiNamedElement> {
        val searchService = project.service<VoqalSearchService>()
        return descendants()
            .filter { it is PsiNamedElement }
            .filter { searchService.isClass(it) }
            .map { it as PsiNamedElement }.toList()
    }

    fun deleteFile(project: Project, file: VirtualFile) {
        ApplicationManager.getApplication().runWriteAction(Computable {
            file.delete(project)
        })
    }

    fun getVirtualFile(className: String, subDirPath: String = ""): VirtualFile {
        val currentLang = getCurrentLang()
        val fileExt = getFileExtensionForLanguage(currentLang)
        val codeFile = File("src/test/resources/$fileExt/$className.$fileExt")
        val fileCode = codeFile.readText().replace("\r\n", "\n")
        return LightVirtualFile("$subDirPath$className.$fileExt", currentLang, fileCode)
    }

    fun getPsiFile(project: Project, virtualFile: VirtualFile): PsiFile {
        return ReadAction.compute(ThrowableComputable {
            PsiManager.getInstance(project).findFile(virtualFile)!!
        })
    }

    fun getCurrentLang(): Language {
        return Language.findLanguageByID(System.getenv("VQL_LANG"))
            ?: throw IllegalStateException("Language not found")
    }

    fun createProjectFileContext(
        fileName: String,
        languageId: String? = null,
        content: String = ""
    ): ProjectFileContext {
        val file = if (languageId != null) {
            val lang = Language.findLanguageByID(languageId)!!
            LightVirtualFile(fileName, lang, content)
        } else {
            LightVirtualFile(fileName, content)
        }
        return ProjectFileContext(file)
    }
}
