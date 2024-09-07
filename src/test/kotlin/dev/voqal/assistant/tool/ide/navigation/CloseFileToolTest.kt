package dev.voqal.assistant.tool.ide.navigation

import com.aallam.openai.api.chat.FunctionCall
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import dev.voqal.JBTest
import dev.voqal.services.VoqalToolService
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking

class CloseFileToolTest : JBTest() {

    fun `test close file shortcut1`() {
        val f1 = myFixture.addFileToProject("Main.java", "")
        myFixture.openFileInEditor(f1.virtualFile)

        val tool = CloseFileTool()
        val call1 = FunctionCall(
            nameOrNull = CloseFileTool.NAME,
            argumentsOrNull = JsonObject().put("directive", "close main.").toString()
        )
        assertTrue(tool.canShortcut(project, call1))

        val call2 = FunctionCall(
            nameOrNull = CloseFileTool.NAME,
            argumentsOrNull = JsonObject().put("directive", "close Main,").toString()
        )
        assertTrue(tool.canShortcut(project, call2))
    }

    fun `test close file in non active editor`(): Unit = runBlocking {
        val f1 = myFixture.addFileToProject("Main.java", "")
        val f2 = myFixture.addFileToProject("HelloWorld.java", "")

        myFixture.openFileInEditor(f1.virtualFile)
        myFixture.openFileInEditor(f2.virtualFile)

        assertEquals(FileEditorManager.getInstance(project).openFiles.size, 2)

        project.service<VoqalToolService>().blindExecute(
            CloseFileTool(), JsonObject().put("directive", "close MAIN")
        )

        assertEquals(FileEditorManager.getInstance(project).openFiles.size, 1)
        assertEquals(FileEditorManager.getInstance(project).openFiles.first().name, f2.name)
    }
}
