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
import dev.voqal.services.getClasses
import dev.voqal.services.getFields
import dev.voqal.services.getFunctions

/**
 * A suite to test the addition of fields to source code.
 */
class AddFieldSuite : BenchmarkSuite {

    /**
     * Add a map field with specific name and type.
     */
    @SupportLanguages(languages = ["JAVA", "kotlin", "Groovy"]) //limit to JVM langs for now
    fun `add a map field called users with string key string value`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "EmptyClass"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyFields(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyFields(className: String, psiFile: PsiFile) {
        this.promise.future().onSuccess {
            checkPsiFileErrors(psiFile, it, setOf("Cannot resolve symbol 'Map'", "Cannot resolve symbol 'HashMap'"))
            checkTextContains(
                listOf(
                    "mapOf<String, String>()",
                    "mutableMapOf<String, String>()",
                    "Map<String, String>"
                ),
                psiFile.virtualFile, it
            )

            val psiClasses = psiFile.getClasses()
            if (psiClasses.size != 1) {
                it.fail("Classes found: " + psiClasses.size)
            } else if (psiClasses.firstOrNull()?.name != className) {
                it.fail("Class name: " + psiClasses.firstOrNull()?.name)
            } else {
                it.success("Found class: $className")
            }

            val psiFields = psiFile.getFields()
            if (psiFields.size != 1) {
                it.fail("Fields found: " + psiFields.size)
            } else {
                it.success("Found 1 field")
            }
            val fieldName = psiFields.firstOrNull()?.name
            if (fieldName != "users") {
                it.fail("Field name: $fieldName")
            } else {
                it.success("Found field: $fieldName")
            }

            //ensure no extra functions
            val psiFunctions = psiFile.getFunctions()
            if (psiFunctions.isNotEmpty()) {
                it.fail("Functions found: " + psiFunctions.size)
            } else {
                it.success("No functions found")
            }

            it.testFinished()
        }
    }
}
