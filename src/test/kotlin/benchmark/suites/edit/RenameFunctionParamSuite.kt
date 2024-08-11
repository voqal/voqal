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
 * A suite to test the renaming of function parameters in source code.
 */
class RenameFunctionParamSuite : BenchmarkSuite {

    /**
     * Rename a single parameter in a function.
     */
    fun `rename parameter x to first param`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "AddMethod"
        val lang = getCurrentLang()
        val virtualFile = if (lang.id == "go") {
            getVirtualFile(className, "main/") //Go requires package
        } else {
            getVirtualFile(className)
        }
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        val expectedTexts = when (lang.id) {
            "go" -> listOf("(firstParam int, y int)", "return firstParam + y")
            "JAVA" -> listOf("(int firstParam, int y)", "return firstParam + y")
            "kotlin" -> listOf("(firstParam: Int, y: Int)", "return firstParam + y")
            "Python" -> listOf("(self, first_param, y)", "return first_param + y")
            "JavaScript" -> listOf("(firstParam, y)", "return firstParam + y")
            else -> throw UnsupportedOperationException("Language not supported")
        }
        command.verifyParamRenames(className, psiFile, expectedTexts)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    /**
     * Rename two parameters in a function.
     */
    fun `rename parameters x and y to a and b`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "AddMethod"
        val lang = getCurrentLang()
        val virtualFile = if (lang.id == "go") {
            getVirtualFile(className, "main/") //Go requires package
        } else {
            getVirtualFile(className)
        }
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        val expectedTexts = when (lang.id) {
            "go" -> listOf("(a int, b int)", "return a + b")
            "JAVA" -> listOf("(int a, int b)", "return a + b")
            "kotlin" -> listOf("(a: Int, b: Int)", "return a + b")
            "Python" -> listOf("(self, a, b)", "return a + b")
            "JavaScript" -> listOf("(a, b)", "return a + b")
            else -> throw UnsupportedOperationException("Language not supported")
        }
        command.verifyParamRenames(className, psiFile, expectedTexts)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyParamRenames(
        className: String,
        psiFile: PsiFile,
        expectedTexts: List<String>
    ) {
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

            expectedTexts.forEach { expectedText ->
                checkTextContains(expectedText, psiFile.virtualFile, it)
            }

            it.testFinished()
        }
    }
}
