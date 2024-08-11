package dev.voqal.assistant.tool.ide

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class SelectLinesToolTest {

    @Test
    fun testSelectLinesTenToOneHundred() {
        val intent = SelectLinesTool()
        val result = intent.attemptIntentExtract("select lines ten to one hundred")
        assertEquals(SelectLinesTool.NAME, result?.first)
        assertEquals("10", result?.second?.get("startLine"))
        assertEquals("100", result?.second?.get("endLine"))
    }

    @Test
    fun testSelectLines() {
        val intent = SelectLinesTool()
        val result = intent.attemptIntentExtract("select lines 1 to 2")
        assertEquals(SelectLinesTool.NAME, result?.first)
        assertEquals("1", result?.second?.get("startLine"))
        assertEquals("2", result?.second?.get("endLine"))
    }

    @Test
    fun testSelectTheLines() {
        val intent = SelectLinesTool()
        val result = intent.attemptIntentExtract("select the lines 1 to 2")
        assertEquals(SelectLinesTool.NAME, result?.first)
        assertEquals("1", result?.second?.get("startLine"))
        assertEquals("2", result?.second?.get("endLine"))
    }
}
