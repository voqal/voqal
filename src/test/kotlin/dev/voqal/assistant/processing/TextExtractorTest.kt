package dev.voqal.assistant.processing

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class TextExtractorTest {

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
        assertNotNull(TextExtractor.findText(needle, text))
    }

    @Test
    fun cancelVuiInteraction() {
        val responseCode = "```{\"cancel\": true}```"
        assertTrue(TextExtractor.checkForVuiInteraction("cancel", responseCode))
    }

    @Test
    fun cancelVuiInteraction2() {
        val responseCode = "```{\"cancel\": true}```\n"
        assertTrue(TextExtractor.checkForVuiInteraction("cancel", responseCode))
    }
}
