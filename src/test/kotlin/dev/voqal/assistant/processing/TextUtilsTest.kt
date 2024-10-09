package dev.voqal.assistant.processing

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class TextUtilsTest {

    @Test
    fun findText() {
        val text = """
            public class Main {

                public void aFunction() {
                    printHelloWorld();
                }

                public void printHelloWorld() {
                    System.out.println("Hello World");
                }

                public void anotherFunction() {
                    printHelloWorld();
                }
            }
        """.trimIndent()
        val codeBlock = """
            ```
            public void printHelloWorld() {
                System.out.println("Hello World");
            }
            ```
        """.trimIndent()
        val needle = CodeExtractor.extractCodeBlock(codeBlock)
        assertNotNull(TextUtils.findText(needle, text))
    }

    @Test
    fun cancelVuiInteraction() {
        val responseCode = "```{\"cancel\": true}```"
        assertTrue(TextUtils.checkForVuiInteraction("cancel", responseCode))
    }

    @Test
    fun cancelVuiInteraction2() {
        val responseCode = "```{\"cancel\": true}```\n"
        assertTrue(TextUtils.checkForVuiInteraction("cancel", responseCode))
    }
}
