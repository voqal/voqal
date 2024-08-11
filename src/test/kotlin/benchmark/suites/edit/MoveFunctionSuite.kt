package benchmark.suites.edit

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import benchmark.model.context.VirtualFileContext
import com.intellij.psi.PsiFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.assistant.context.code.ViewingCode
import dev.voqal.config.settings.PromptSettings

class MoveFunctionSuite : BenchmarkSuite {

    fun `move the start game function to be below the exit game function`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "AdventureGame"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text
        val linesOfCode = code.lines().size

        val expectedOrder = mutableListOf(
            "gameLoop",
            "showStatus",
            "showOptions",
            "handlePlayerChoice",
            "explore",
            "findGold",
            "encounterEnemy",
            "findNothing",
            "findItem",
            "triggerRandomEvent",
            "rest",
            "visitShop",
            "buyItem",
            "viewInventory",
            "craftItems",
            "completeQuests",
            "useItemInCombat",
            "exitGame",
            "startGame",
            "checkGameOver"
        )
        if (lang.id == "go") {
            expectedOrder += listOf("prompt", "promptInt")
        }
        command.verifyFunctionMove(className, psiFile, linesOfCode, expectedOrder)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    fun `move start game function to between view inventory and craft items functions`(command: BenchmarkPromise): List<VoqalContext> {
        val className = "AdventureGame"
        val lang = getCurrentLang()
        val virtualFile = getVirtualFile(className)
        val psiFile = getPsiFile(command.project, virtualFile)
        val code = psiFile.text
        val linesOfCode = code.lines().size

        val expectedOrder =  mutableListOf(
            "gameLoop",
            "showStatus",
            "showOptions",
            "handlePlayerChoice",
            "explore",
            "findGold",
            "encounterEnemy",
            "findNothing",
            "findItem",
            "triggerRandomEvent",
            "rest",
            "visitShop",
            "buyItem",
            "viewInventory",
            "startGame",
            "craftItems",
            "completeQuests",
            "useItemInCombat",
            "exitGame",
            "checkGameOver"
        )
        if (lang.id == "go") {
            expectedOrder += listOf("prompt", "promptInt")
        }
        command.verifyFunctionMove(className, psiFile, linesOfCode, expectedOrder)

        return listOf(
            VirtualFileContext(virtualFile),
            ViewingCode(code, language = lang.id),
            PromptSettingsContext(PromptSettings(promptName = "Edit Mode"))
        )
    }

    private fun BenchmarkPromise.verifyFunctionMove(
        className: String,
        psiFile: PsiFile,
        linesOfCode: Int,
        expectedOrder: List<String>
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

            val updatedLinesOfCode = psiFile.text.lines().size
            if (updatedLinesOfCode != linesOfCode) {
                it.fail("Expected $linesOfCode lines of code, found $updatedLinesOfCode")
            } else {
                it.success("Found $linesOfCode lines of code")
            }

            val psiFunctions = psiFile.getFunctions()
                .filterNot { it.name == "constructor" }
            if (psiFunctions.size != 20) {
                it.fail("Expected 20 functions, found ${psiFunctions.size}")
            } else {
                it.success("Found 20 functions")
            }

            val functionNames = psiFunctions.map { it.name }
            val remainingExpected = expectedOrder.toMutableList()
            functionNames.forEachIndexed { index, functionName ->
                if (remainingExpected.isNotEmpty() && functionName == remainingExpected.first()) {
                    remainingExpected.removeAt(0)
                    it.success("Found function at index $index to be $functionName in the correct order")
                } else {
                    remainingExpected.removeAll { it == functionName }
                    it.fail("Function $functionName at index $index was not expected")
                }
            }
            if (remainingExpected.isNotEmpty()) {
                remainingExpected.forEach { functionName ->
                    it.fail("Function $functionName was not found")
                }
            }

            it.testFinished()
        }
    }
}
