package dev.voqal.assistant.tool.text

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DeleteLinesToolTest {

    @Test
    fun testDeleteLinesTenToOneHundred() {
        val intent = DeleteLinesTool()
        val result = intent.attemptIntentExtract("delete lines ten to one hundred")
        assertEquals(DeleteLinesTool.NAME, result?.first)
        assertEquals("10", result?.second?.get("startLine"))
        assertEquals("100", result?.second?.get("endLine"))
    }

    @Test
    fun testDeleteLines() {
        val intent = DeleteLinesTool()
        val result = intent.attemptIntentExtract("delete lines 1 to 2")
        assertEquals(DeleteLinesTool.NAME, result?.first)
        assertEquals("1", result?.second?.get("startLine"))
        assertEquals("2", result?.second?.get("endLine"))
    }

    @Test
    fun testDeleteTheLines() {
        val intent = DeleteLinesTool()
        val result = intent.attemptIntentExtract("delete the lines 1 to 2")
        assertEquals(DeleteLinesTool.NAME, result?.first)
        assertEquals("1", result?.second?.get("startLine"))
        assertEquals("2", result?.second?.get("endLine"))
    }
}
