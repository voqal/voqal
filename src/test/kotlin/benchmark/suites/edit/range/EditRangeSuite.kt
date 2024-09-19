package benchmark.suites.edit.range

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.*
import benchmark.model.metadata.SupportLanguages
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.psi.PsiFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings
import dev.voqal.services.getFunctions
import dev.voqal.status.VoqalStatus

class EditRangeSuite : BenchmarkSuite {

    @SupportLanguages(languages = ["JAVA", "kotlin", "Groovy"])
    fun `implement function to multiply x by the multiplier and add the offset`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        val className = "TestFileAccess"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text

        val exactRange = ReadAction.compute(ThrowableComputable {
            psiFile.getFunctions().find { it.name == "calculate" }!!.textRange
        })
        val startLine = ReadAction.compute(ThrowableComputable {
            psiFile.fileDocument.getLineNumber(exactRange.startOffset)
        })
        val endLine = ReadAction.compute(ThrowableComputable {
            psiFile.fileDocument.getLineNumber(exactRange.endOffset)
        })
        val fullMethodRange = ProperTextRange(
            psiFile.fileDocument.getLineStartOffset(startLine),
            psiFile.fileDocument.getLineEndOffset(endLine)
        )

        command.verifyEditRange(code, psiFile)

        return listOf(
            VoqalStatusContext(VoqalStatus.EDITING),
            VisibleRangeContext(fullMethodRange),
            OpenFileContext(virtualFile),
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyEditRange(originalCode: String, psiFile: PsiFile) {
        this.promise.future().onSuccess {
            checkPsiFileErrors(psiFile, it)
            checkTextContains("x * a + b", psiFile.virtualFile, it)

            //ensure no extra changes
            val codeBeforeFunc = psiFile.text.substringBefore("public int calculate")
            if (codeBeforeFunc == originalCode.substringBefore("public int calculate")) {
                it.success("No extra changes")
            } else {
                it.fail("Extra change: $codeBeforeFunc")
            }

            it.testFinished()
        }
    }
}
