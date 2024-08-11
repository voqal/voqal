package benchmark.suites.edit

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import benchmark.model.metadata.SupportLanguages
import com.intellij.psi.PsiFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings

class MissingImportsSuite : BenchmarkSuite {

    @SupportLanguages(languages = ["JAVA", "kotlin", "Groovy", "go", "Python"]) //everything but javascript
    fun `add missing imports`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "MissingImports"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyMissingImports(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyMissingImports(className: String, psiFile: PsiFile) {
        this.promise.future().onSuccess {
            checkPsiFileErrors(psiFile, it)

            val psiClasses = psiFile.getClasses()
            if (psiClasses.size != 1) {
                it.fail("Classes found: " + psiClasses.size)
            } else if (psiClasses.firstOrNull()?.name != className) {
                it.fail("Class name: " + psiClasses.firstOrNull()?.name)
            } else {
                it.success("Found class: $className")
            }

            val psiFunctions = psiFile.getFunctions()
            if (psiFunctions.size != 5) {
                it.fail("Expected 5 functions, found ${psiFunctions.size}")
            } else {
                it.success("Found 5 functions")
            }

            if (psiFile.language.id == "Python") {
                checkTextContains("import random", psiFile, this)
                checkTextContains("import datetime", psiFile, this)
                checkTextContains("import os", psiFile, this)
                checkTextContains("import date", psiFile, this)
                checkTextContains("import time", psiFile, this)
            } else if (psiFile.language.id == "go") {
                checkTextContains(""""fmt""", psiFile, this)
                checkTextContains(""""math/rand""", psiFile, this)
                checkTextContains(""""os""", psiFile, this)
                checkTextContains(""""time""", psiFile, this)
            } else {
                checkTextContains("import java.time.LocalDate", psiFile, this)
                checkTextContains("import java.time.ZoneId", psiFile, this)
                checkTextContains("import java.io.File", psiFile, this)
                checkTextContains("import java.text.SimpleDateFormat", psiFile, this)

                if (psiFile.text.contains("import java.util.*")) {
                    checkTextContains("import java.util.*", psiFile, this)
                } else {
                    checkTextContains("import java.util.Date", psiFile, this)
                    checkTextContains("import java.util.Random", psiFile, this)
                }
            }

            it.testFinished()
        }
    }
}
