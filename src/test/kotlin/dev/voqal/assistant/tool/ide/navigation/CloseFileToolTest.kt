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

        assertEquals(2, FileEditorManager.getInstance(project).openFiles.size)

        project.service<VoqalToolService>().blindExecute(
            CloseFileTool(), JsonObject().put("directive", "close MAIN")
        )

        assertEquals(1, FileEditorManager.getInstance(project).openFiles.size)
        assertEquals(f2.name, FileEditorManager.getInstance(project).openFiles.first().name)
    }

    fun `test close file non active camel case`(): Unit = runBlocking {
        val f1 = myFixture.addFileToProject("GoodbyeWorld.java", "")
        val f2 = myFixture.addFileToProject("HelloWorld.java", "")

        myFixture.openFileInEditor(f1.virtualFile)
        myFixture.openFileInEditor(f2.virtualFile)

        assertEquals(2, FileEditorManager.getInstance(project).openFiles.size)

        project.service<VoqalToolService>().blindExecute(
            CloseFileTool(), JsonObject().put("directive", "close goodbye world")
        )

        assertEquals(1, FileEditorManager.getInstance(project).openFiles.size)
        assertEquals(f2.name, FileEditorManager.getInstance(project).openFiles.first().name)
    }

    fun `test close file non active path`(): Unit = runBlocking {
        val f1 = myFixture.addFileToProject("GoodbyeWorld.java", "")
        val f2 = myFixture.addFileToProject("HelloWorld.java", "")

        myFixture.openFileInEditor(f1.virtualFile)
        myFixture.openFileInEditor(f2.virtualFile)

        assertEquals(2, FileEditorManager.getInstance(project).openFiles.size)

        project.service<VoqalToolService>().blindExecute(
            CloseFileTool(), JsonObject().put("directive", "close src/goodbyeworld")
        )

        assertEquals(1, FileEditorManager.getInstance(project).openFiles.size)
        assertEquals(f2.name, FileEditorManager.getInstance(project).openFiles.first().name)
    }
}
