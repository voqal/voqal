package dev.voqal.assistant.tool.ide.navigation

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ScrollToolTest {

    // scroll all the way to the bottom

    @Test
    fun testScrollIntent() {
        val testData = listOf(
            "scroll up to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "scroll")),
            "scroll up to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "scroll")),
            "scroll to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "scroll")),
            "scroll down to the bottom" to Pair(
                "scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "scroll")
            ),
            "scroll down to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "scroll")),
            "scroll to the bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "scroll")),
            "scroll up to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "scroll")),
            "scroll up to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "scroll")),
            "scroll to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "scroll")),
            "scroll down to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "scroll")),
            "scroll down to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "scroll")),
            "scroll to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "scroll")),
            "scroll to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "scroll")),
            "scroll to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "scroll")),
            "scroll to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "scroll")),
            "scroll to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "scroll")),
            "scroll up" to Pair("scroll", mapOf("scrollDirection" to "UP", "scrollType" to "scroll")),
            "scroll down" to Pair("scroll", mapOf("scrollDirection" to "DOWN", "scrollType" to "scroll"))
        )

        val scrollTool = ScrollTool()
        testData.forEach { (input, expected) ->
            val result = scrollTool.attemptIntentExtract(input)
            assertEquals(expected, result, input)
        }
    }

    @Test
    fun testPageIntent() {
        val testData = listOf(
            "page up to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "page")),
            "page up to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "page")),
            "page to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "page")),
            "page down to the bottom" to Pair(
                "scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "page")
            ),
            "page down to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "page")),
            "page to the bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "page")),
            "page up to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "page")),
            "page up to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "page")),
            "page to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "page")),
            "page down to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "page")),
            "page down to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "page")),
            "page to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "page")),
            "page to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "page")),
            "page to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "page")),
            "page to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "page")),
            "page to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "page")),
            "page up" to Pair("scroll", mapOf("scrollDirection" to "UP", "scrollType" to "page")),
            "page down" to Pair("scroll", mapOf("scrollDirection" to "DOWN", "scrollType" to "page"))
        )

        val scrollTool = ScrollTool()
        testData.forEach { (input, expected) ->
            val result = scrollTool.attemptIntentExtract(input)
            assertEquals(expected, result, input)
        }
    }

    @Test
    fun testGoIntent() {
        val testData = listOf(
            "go up to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "go")),
            "go up to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "go")),
            "go to the top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "go")),
            "go down to the bottom" to Pair(
                "scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "go")
            ),
            "go down to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "go")),
            "go to the bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "go")),
            "go up to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "go")),
            "go up to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "go")),
            "go to the start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "go")),
            "go down to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "go")),
            "go down to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "go")),
            "go to the end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "go")),
            "go to top" to Pair("scroll", mapOf("scrollDirection" to "TOP", "scrollType" to "go")),
            "go to bottom" to Pair("scroll", mapOf("scrollDirection" to "BOTTOM", "scrollType" to "go")),
            "go to start" to Pair("scroll", mapOf("scrollDirection" to "START", "scrollType" to "go")),
            "go to end" to Pair("scroll", mapOf("scrollDirection" to "END", "scrollType" to "go")),
        )

        val scrollTool = ScrollTool()
        testData.forEach { (input, expected) ->
            val result = scrollTool.attemptIntentExtract(input)
            assertEquals(expected, result, input)
        }
    }
}
