package dev.voqal.services.mode

import com.intellij.openapi.components.service
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.context.DeveloperContext
import dev.voqal.assistant.tool.ide.AddBreakpointsTool
import dev.voqal.assistant.tool.ide.navigation.OpenFileTool
import dev.voqal.assistant.tool.ide.PlayProgramTool
import dev.voqal.assistant.tool.ide.StopProgramTool
import dev.voqal.services.VoqalToolService
import io.vertx.core.json.JsonObject
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class VoqalIdleServiceTest : JBTest() {

    fun testNoCommandProvided() {
        val toolService = project.service<VoqalToolService>()
        val result = toolService.getAvailableTools()
        assertEquals(52, result.size)
    }

    fun testPlayCommandRecognition() {
        val toolService = project.service<VoqalToolService>()
        val directive = mock<VoqalDirective>()
        whenever(directive.developer).thenReturn(DeveloperContext(transcription = "play program"))
        val result = toolService.getAvailableTools()
        assertTrue(result.containsKey(PlayProgramTool.NAME))// { result.toString() }
    }

    fun testStopCommandRecognition() {
        val toolService = project.service<VoqalToolService>()
        val directive = mock<VoqalDirective>()
        whenever(directive.developer).thenReturn(DeveloperContext(transcription = "stop the program"))
        val result = toolService.getAvailableTools()
        assertTrue(result.containsKey(StopProgramTool.NAME))// { result.toString() }
    }

    fun testOpenCommandRecognition() {
        val toolService = project.service<VoqalToolService>()
        val directive = mock<VoqalDirective>()
        whenever(directive.developer).thenReturn(DeveloperContext(transcription = "open the file"))
        val result = toolService.getAvailableTools()
        assertTrue(result.containsKey(OpenFileTool.NAME))// { result.toString() }
    }

    fun testBreakpointCommandRecognition() {
        val toolService = project.service<VoqalToolService>()
        val directive = mock<VoqalDirective>()
        whenever(directive.developer).thenReturn(DeveloperContext(transcription = "add a breakpoint"))
        val result = toolService.getAvailableTools()
        assertTrue(result.containsKey(AddBreakpointsTool.NAME))// { result.toString() }
    }

    fun testFixIllegalDollarEscape() {
        val json = "{\"name\": \"\\$\"}"
        val result = JsonObject(VoqalToolService.fixIllegalDollarEscape(json))
        assertEquals("\$", result.getString("name"))

        val json2 = "{\"name\": \"\\\$\"}"
        val result2 = JsonObject(VoqalToolService.fixIllegalDollarEscape(json2))
        assertEquals("\$", result2.getString("name"))

        val json3 = "{\"name\": \"$\"}"
        val result3 = JsonObject(VoqalToolService.fixIllegalDollarEscape(json3))
        assertEquals("$", result3.getString("name"))
    }
}
