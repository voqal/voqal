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
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory

class InlineFunctionSuite : BenchmarkSuite {

    fun `inline the code in the complex calculation function and then delete the function`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "InlineMathOperations"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        command.verifyInline(className, psiFile)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyInline(className: String, psiFile: PsiFile) {
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

            val scriptEngine = GroovyScriptEngineFactory().scriptEngine
            val calculateResult1 = psiFunctions.find { it.name == "calculateResult1" }
            if (calculateResult1 == null) {
                it.fail("calculateResult1 not found")
            } else {
                val functionBody = calculateResult1.getCodeBlock()

                if (functionBody != null) {
                    val x = 5
                    val y = 6
                    val expression = functionBody.text.replace("x", x.toString()).replace("y", y.toString())
                        .replace("val ", "") //de-kotlin
                        .replace(":=", "=") //de-go
                        .replace("const ", "") //de-javascript

                    try {
                        val result = scriptEngine.eval(expression)
                        if (result == 52) {
                            success("calculateResult1 is correct")
                        } else {
                            fail("calculateResult1 is incorrect: $result")
                        }
                    } catch (e: Throwable) {
                        fail(e.toString())
                    }
                } else {
                    it.fail("Missing calculateResult1 function body")
                }
            }
            val calculateResult2 = psiFunctions.find { it.name == "calculateResult2" }
            if (calculateResult2 == null) {
                it.fail("calculateResult2 not found")
            } else {
                val functionBody = calculateResult2.getCodeBlock()

                if (functionBody != null) {
                    val p = 5
                    val q = 6
                    val expression = functionBody.text.replace("p", p.toString()).replace("q", q.toString())
                        .replace("val", "") //de-kotlin
                        .replace(":=", "=") //do-go
                        .replace("const ", "") //de-javascript
                        .replace("5roduct", "product")

                    try {
                        val result = scriptEngine.eval(expression)
                        if (result == 84) {
                            success("calculateResult2 is correct")
                        } else {
                            fail("calculateResult2 is incorrect: $result")
                        }
                    } catch (e: Throwable) {
                        fail(e.toString())
                    }
                } else {
                    it.fail("Missing calculateResult2 function body")
                }
            }

            it.testFinished()
        }
    }
}
