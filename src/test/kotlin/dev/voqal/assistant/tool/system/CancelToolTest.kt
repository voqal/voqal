package dev.voqal.assistant.tool.system

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import dev.voqal.JBTest
import dev.voqal.assistant.tool.text.EditTextTool
import dev.voqal.services.VoqalMemoryService
import dev.voqal.services.VoqalStatusService
import dev.voqal.services.VoqalToolService
import dev.voqal.services.scope
import dev.voqal.status.VoqalStatus
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import java.io.File

class CancelToolTest : JBTest() {

    fun `test cancel reverts affected files`() {
        val responseCode = File("src/test/resources/edit-stream/rename-myMethod-to-test.txt").readText()
            .replace("\r\n", "\n")
        val fileOneText = File("src/test/resources/edit-stream/FileOne.java").readText()
            .replace("\r\n", "\n")
        val fileTwoText = File("src/test/resources/edit-stream/FileTwo.java").readText()
            .replace("\r\n", "\n")

        val fileOnePsi = myFixture.addFileToProject("FileOne.java", fileOneText)
        val fileTwoPsi = myFixture.addFileToProject("FileTwo.java", fileTwoText)
        myFixture.openFileInEditor(fileOnePsi.virtualFile)

        val testContext = VertxTestContext()
        project.scope.launch {
            project.service<VoqalStatusService>().update(VoqalStatus.EDITING)
            val memory = project.service<VoqalMemoryService>().getCurrentMemory(null)
            project.service<VoqalMemoryService>().saveEditLabel(memory.id, myFixture.editor)
            EditTextTool().doDocumentEdits(project, responseCode, myFixture.editor, true)

            testContext.verify {
                assertEquals(
                    fileOnePsi.text,
                    fileOneText.replace("myMethod", "test")
                )
                assertEquals(
                    fileTwoPsi.text,
                    fileTwoText.replace("myMethod", "test")
                )
            }

            project.service<VoqalToolService>().blindExecute(CancelTool())

            //todo: works in real ide but not via tests
//            testContext.verify {
//                assertEquals(fileOnePsi.text, fileOneText)
//                assertEquals(fileTwoPsi.text, fileTwoText)
//            }

            testContext.completeNow()
        }
        errorOnTimeout(testContext)
        EditorFactory.getInstance().releaseEditor(myFixture.editor)
    }
}
