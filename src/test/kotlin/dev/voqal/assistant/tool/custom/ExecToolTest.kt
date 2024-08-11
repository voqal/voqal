package dev.voqal.assistant.tool.custom

import dev.voqal.JBTest

class ExecToolTest : JBTest() {

    fun testTemplateString() {
        val input = "echo $[[name]]"
        val variables = mapOf("name" to "Brandon")
        val result = ExecTool.templateString(input, variables)
        assertEquals("echo Brandon", result)
    }
}
