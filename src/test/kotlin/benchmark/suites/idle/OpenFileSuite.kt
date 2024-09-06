package benchmark.suites.idle

import benchmark.model.BenchmarkPromise
import benchmark.model.BenchmarkSuite
import benchmark.model.context.PromptSettingsContext
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.isFile
import dev.voqal.assistant.context.VoqalContext
import dev.voqal.config.settings.PromptSettings

/**
 * A suite to test the opening of files.
 */
class OpenFileSuite : BenchmarkSuite {

    /**
     * Open a single file (via name) in a project of two files.
     */
    fun `open readme`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        command.verifyOpenFiles(listOf("README.MD"))

        return listOf(
            createProjectFileContext("test1.txt", content = "This is test file 1"),
            createProjectFileContext("README.MD", content = "This is a readme file"),
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    /**
     * Open two files (via name) in a project of two files.
     */
    fun `open test1 and test2`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        command.verifyOpenFiles(listOf("test1.txt", "test2.txt"))

        return listOf(
            createProjectFileContext("test1.txt", content = "This is test file 1"),
            createProjectFileContext("test2.txt", content = "This is test file 2"),
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    /**
     * Open three files (via semantic search) in a project of eight files.
     */
    fun `open the files named after fruits`(
        command: BenchmarkPromise
    ): List<VoqalContext> {
        command.verifyOpenFiles(listOf("apple.txt", "banana.txt", "cherry.txt"))

        //5 random files
        val files = listOf("a11.txt", "b22.txt", "c33.txt", "d44.txt", "e55.txt")
        //3 files named after fruits
        val fruits = listOf("apple.txt", "banana.txt", "cherry.txt")

        return (files + fruits).map { createProjectFileContext(it) } + listOf(
            PromptSettingsContext(PromptSettings(promptName = "Idle Mode"))
        )
    }

    private fun BenchmarkPromise.verifyOpenFiles(expectedFiles: List<String>) {
        this.promise.future().onSuccess {
            val openFiles = FileEditorManager.getInstance(this.project).openFiles
            if (openFiles.size != expectedFiles.size) {
                it.fail("Open files: " + openFiles.size)
            } else {
                it.success("Opened ${expectedFiles.size} files")
            }

            val fileNames = openFiles.map { it.name }
            expectedFiles.forEach { expectedFile ->
                if (!fileNames.contains(expectedFile)) {
                    it.fail("Opened file not found: $expectedFile")
                } else {
                    it.success("Opened file: $expectedFile")
                }
            }

            //clean up
            ProjectFileIndex.getInstance(project).iterateContent {
                if (it.isFile) {
                    FileEditorManager.getInstance(this.project).closeFile(it)
                    deleteFile(project, it)
                }
                true
            }

            it.testFinished()
        }
    }
}
