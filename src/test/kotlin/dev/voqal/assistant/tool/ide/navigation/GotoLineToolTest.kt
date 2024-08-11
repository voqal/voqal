package dev.voqal.assistant.tool.ide.navigation

import dev.voqal.JBTest

class GotoLineToolTest : JBTest() {

    fun testGoToLineTwentyFive() {
        val rawString = "go to line twenty five"
        val intent = GotoLineTool()
        val result = intent.attemptIntentExtract(rawString)
        assertEquals(GotoLineTool.NAME, result?.first)
        assertEquals("25", result?.second?.get("lineNumber"))
    }

    fun testGoToLine25() {
        val rawString = "go to line 25"
        val intent = GotoLineTool()
        val result = intent.attemptIntentExtract(rawString)
        assertEquals(GotoLineTool.NAME, result?.first)
        assertEquals("25", result?.second?.get("lineNumber"))
    }

    fun testGoToLineOneHundredAndFive() {
        val rawString = "go to line one hundred and five"
        val intent = GotoLineTool()
        val result = intent.attemptIntentExtract(rawString)
        assertEquals(GotoLineTool.NAME, result?.first)
        assertEquals("105", result?.second?.get("lineNumber"))
    }

    fun testGoToLineOneThousandAndTwentyTwo() {
        val rawString = "go to line one thousand and twenty two"
        val intent = GotoLineTool()
        val result = intent.attemptIntentExtract(rawString)
        assertEquals(GotoLineTool.NAME, result?.first)
        assertEquals("1022", result?.second?.get("lineNumber"))
    }

    fun testGoToLineOneThousandOneHundredAndTwentyTwo() {
        val rawString = "go to line one thousand one hundred and twenty two"
        val intent = GotoLineTool()
        val result = intent.attemptIntentExtract(rawString)
        assertEquals(GotoLineTool.NAME, result?.first)
        assertEquals("1122", result?.second?.get("lineNumber"))
    }
}
