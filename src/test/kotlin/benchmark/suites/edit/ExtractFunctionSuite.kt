package benchmark.suites.edit

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import com.intellij.psi.PsiFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.getClasses
import dev.voqal.services.getCodeBlock
import dev.voqal.services.getFunctions

class ExtractFunctionSuite : BenchmarkSuite {

    fun `extract the repeated code into a single function called complex calculation that has two params`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "MathOperations"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyExtraction(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyExtraction(className: String, psiFile: PsiFile) {
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
            if (psiFunctions.size != 3) {
                it.fail("Expected 3 functions, found ${psiFunctions.size}")
            } else {
                it.success("Found 3 functions")
            }
            val calculateResult1 = psiFunctions.find { it.name == "calculateResult1" }
            if (calculateResult1 == null) {
                it.fail("calculateResult1 not found")
            } else {
                val functionBody = calculateResult1.getCodeBlock()
                val checks = listOf(
                    "return complexCalculation(x, y) + 10",
                    "return this.complexCalculation(x, y) + 10",
                    "return self.complexCalculation(x, y) + 10",
                    "return 10 + complexCalculation(x, y)",
                    "return 10 + this.complexCalculation(x, y)",
                    "return 10 + self.complexCalculation(x, y)",

                    //python preferences
                    "return self.complex_calculation(x, y) + 10",
                    "return 10 + self.complex_calculation(x, y)",
                    "return (sum_ + product - difference) + 10",

                    //javascript preferences
                    "return (sum + product - difference) + 10"
                )
                if (functionBody != null) {
                    checkTextContains(checks, functionBody, it)
                } else {
                    it.fail("Missing calculateResult1 function body")
                }
            }
            val calculateResult2 = psiFunctions.find { it.name == "calculateResult2" }
            if (calculateResult2 == null) {
                it.fail("calculateResult2 not found")
            } else {
                val functionBody = calculateResult2.getCodeBlock()
                val checks = listOf(
                    "return complexCalculation(p, q) * 2",
                    "return this.complexCalculation(p, q) * 2",
                    "return self.complexCalculation(p, q) * 2",
                    "return 2 * complexCalculation(p, q)",
                    "return 2 * this.complexCalculation(p, q)",
                    "return 2 * self.complexCalculation(p, q)",

                    //python preferences
                    "return self.complex_calculation(p, q) * 2",
                    "return 2 * self.complex_calculation(p, q)",
                    "return 2 * (sum_ + product - difference)",

                    //javascript preferences
                    "return 2 * (sum + product - difference)"
                )
                if (functionBody != null) {
                    checkTextContains(checks, functionBody, it)
                } else {
                    it.fail("Missing calculateResult2 function body")
                }
            }

            it.testFinished()
        }
    }
}
