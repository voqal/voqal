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
 * A suite to test the modification of function(s) in source code.
 */
class ModifyFunctionSuite : BenchmarkSuite {

    /**
     * Add print() logging to two functions.
     */
    fun `add print logging to the add and subtract functions`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "RemoveMethod"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyPrintLogging(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyPrintLogging(className: String, psiFile: PsiFile) {
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
            val addFunction = psiFunctions.find { it.name == "add" }
            if (addFunction == null) {
                it.fail("Add function not found")
            } else {
                when (psiFile.language.id) {
                    "go" -> checkTextContains(listOf("Printf(", "Println("), addFunction, it)
                    "Python" -> checkTextContains("print(", addFunction, it)
                    "ECMAScript 6" -> checkTextContains("console.log(", addFunction, it)
                    else -> checkTextContains("println(", addFunction, it)
                }
            }
            val subtractFunction = psiFunctions.find { it.name == "subtract" }
            if (subtractFunction == null) {
                it.fail("Subtract function not found")
            } else {
                when (psiFile.language.id) {
                    "go" -> checkTextContains(listOf("Printf(", "Println("), subtractFunction, it)
                    "Python" -> checkTextContains("print(", subtractFunction, it)
                    "ECMAScript 6" -> checkTextContains("console.log(", subtractFunction, it)
                    else -> checkTextContains("println(", subtractFunction, it)
                }
            }

            it.testFinished()
        }
    }
}
