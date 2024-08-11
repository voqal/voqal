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
 * A suite to test the removal of function(s) from source code.
 */
class RemoveFunctionSuite : BenchmarkSuite {

    /**
     * Delete a function in a file with two functions.
     */
    fun `delete the subtract function`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "RemoveMethod"
        val lang = getCurrentLang()
        val virtualFile = if (lang.id == "go") {
            getVirtualFile(className, "main/") //Go requires package
        } else {
            getVirtualFile(className)
        }
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyRemovedFunctions(className, psiFile, listOf("add"))

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    /**
     * Delete two functions separated by a function which is not deleted.
     */
    fun `delete the add and subtract functions`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "ModifyBetween"
        val lang = getCurrentLang()
        val virtualFile = if (lang.id == "go") {
            getVirtualFile(className, "main/") //Go requires package
        } else {
            getVirtualFile(className)
        }
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyRemovedFunctions(className, psiFile, listOf("divide"))

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyRemovedFunctions(
        className: String,
        psiFile: PsiFile,
        expectedFunctions: List<String>
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

            val expectedFunctionCount = expectedFunctions.size
            val psiFunctions = psiFile.getFunctions()
            if (psiFunctions.size != expectedFunctionCount) {
                it.fail("Expected $expectedFunctionCount function, found ${psiFunctions.size}")
            } else {
                it.success("Found $expectedFunctionCount function(s)")
            }
            expectedFunctions.forEach { expectedFunction ->
                val function = psiFunctions.find { it.name == expectedFunction }
                if (function == null) {
                    it.fail("$expectedFunction function not found")
                } else {
                    it.success("$expectedFunction function found")
                }
            }

            it.testFinished()
        }
    }
}
