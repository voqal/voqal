package dev.voqal.assistant.context.code

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ViewingCodeTest {

    @Test
    fun `test omitted code not included`() {
        val codeFile = File("src/test/resources/java/TestFileAccess.java")
        val codeText = codeFile.readText()

        val omittedCode = ViewingCode(
            code = codeText,
            includedLines = listOf(IntRange(0, 4), IntRange(7, 8))
        )
        val json = omittedCode.toJson()
        assertFalse(json.toString().contains("public int calculate(int x)"))
        assertTrue(json.toString().contains("6|    ...omitted..."))
        assertTrue(json.toString().contains("7|    ...omitted..."))
    }
}
