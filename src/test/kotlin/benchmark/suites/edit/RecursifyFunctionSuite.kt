package benchmark.suites.edit

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import com.intellij.psi.PsiFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings

/**
 * A suite to test the recursifying and de-recursifying of functions.
 */
class RecursifyFunctionSuite : BenchmarkSuite {

    fun `rewrite factorial recursive as factorial iterative and find max iterative as find max recursive using no helper functions`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "RFunctions"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyRecursion(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyRecursion(className: String, psiFile: PsiFile) {
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
            if (psiFunctions.size != 2) {
                it.fail("Expected 2 functions, found ${psiFunctions.size}")
            } else {
                it.success("Found 2 functions")
            }
            val factorialIterative = psiFunctions.find { it.name == "factorialIterative" }
            if (factorialIterative == null) {
                it.fail("Factorial iterative function not found")
            } else {
                val functionBody = factorialIterative.getCodeBlock()
                checkNotTextContains("factorialIterative(", functionBody, it)
            }
            val findMaxRecursive = psiFunctions.find { it.name == "findMaxRecursive" }
            if (findMaxRecursive == null) {
                it.fail("Find max recursive function not found")
            } else {
                val functionBody = findMaxRecursive.getCodeBlock()
                checkTextContains("findMaxRecursive(", functionBody, it)
            }

            it.testFinished()
        }
    }
}
