package dev.voqal.assistant.tool.code

import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.chat.ToolId
import com.intellij.openapi.components.service
import com.intellij.testFramework.replaceService
import dev.voqal.JBTest
import dev.voqal.assistant.VoqalDirective
import dev.voqal.assistant.VoqalResponse
import dev.voqal.assistant.context.IdeContext
import dev.voqal.assistant.focus.SpokenTranscript
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.VoqalSearchService
import dev.voqal.services.scope
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.mock

class MoveClassToolTest : JBTest() {

    fun `test java move by fully qualified names`(): Unit = runBlocking {
        val testFile = myFixture.addFileToProject("com/example/Test.java", "package com.example; class Test {}")
        val otherFile = myFixture.addFileToProject("com/test2/Other.java", "package com.test2; class Other {}")
        val srcRoot = otherFile.parent!!.parent!!.parent!!.virtualFile
        assertNotNull(srcRoot)

        val testContext = VertxTestContext()
        val realSearchService = project.service<VoqalSearchService>()
        val mockSearchService = mock<VoqalSearchService>(
            defaultAnswer = {
                if (it.method.name == "getProjectStructureAsMarkdownTree") {
                    return@mock realSearchService.getProjectStructureAsMarkdownTree()
                } else if (it.method.name == "getMainSourceRoot") {
                    return@mock srcRoot
                } else if (it.method.name == "getSourceRoots") {
                    return@mock listOf(srcRoot)
                } else if (it.method.name == "getLanguageForSourceRoot") {
                    return@mock testFile.language
                } else if (it.method.name == "findFile") {
                    return@mock runBlocking {
                        realSearchService.findFile(it.getArgument(0), it.getArgument(1))
                    }
                } else if (it.method.name == "getFlattenedPackages") {
                    return@mock realSearchService.getFlattenedPackages()
                } else if (it.method.name == "getPackageByName") {
                    return@mock realSearchService.getPackageByName(it.getArgument(0), it.getArgument(1))
                } else {
                    TODO()
                }
                Unit
            }
        )
        project.replaceService(VoqalSearchService::class.java, mockSearchService, testRootDisposable)

        val directiveService = project.service<VoqalDirectiveService>()
        val transcription = "move class com.example.Test to com.test2"
        project.scope.launch {
            val directive = directiveService.asDirective(
                SpokenTranscript(transcription, null),
                textOnly = true
            )

            val response = directive.assistant.memorySlice.addMessage(directive)
            assertEquals(1, response.toolCalls.size)
            val toolCall = response.toolCalls[0] as ToolCall.Function
            val functionCall = toolCall.function
            assertEquals(MoveClassTool.NAME, functionCall.name)

            val moveClassTool = MoveClassTool()
            moveClassTool.actionPerformed(JsonObject(functionCall.arguments), directive)

            val oldLocation = realSearchService.findFile("com.example.Test")
            val newLocation = realSearchService.findFile("com.test2.Test")
            testContext.verify {
                assertNull(oldLocation)
                assertNotNull(newLocation)
                assertEquals("/src/com/test2/Test.java", newLocation!!.path)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
    }

    fun `test kotlin move by fully qualified names`(): Unit = runBlocking {
        val testFile = myFixture.addFileToProject("com/example/Test.kt", "package com.example; class Test {}")
        val otherFile = myFixture.addFileToProject("com/test2/Other.kt", "package com.test2; class Other {}")
        val srcRoot = otherFile.parent!!.parent!!.parent!!.virtualFile
        assertNotNull(srcRoot)

        val testContext = VertxTestContext()
        val realSearchService = project.service<VoqalSearchService>()
        val mockSearchService = mock<VoqalSearchService>(
            defaultAnswer = {
                if (it.method.name == "getProjectStructureAsMarkdownTree") {
                    return@mock realSearchService.getProjectStructureAsMarkdownTree()
                } else if (it.method.name == "getMainSourceRoot") {
                    return@mock srcRoot
                } else if (it.method.name == "getSourceRoots") {
                    return@mock listOf(srcRoot)
                } else if (it.method.name == "getLanguageForSourceRoot") {
                    return@mock testFile.language
                } else if (it.method.name == "findFile") {
                    return@mock runBlocking {
                        realSearchService.findFile(it.getArgument(0), it.getArgument(1))
                    }
                } else if (it.method.name == "getFlattenedPackages") {
                    return@mock realSearchService.getFlattenedPackages()
                } else if (it.method.name == "getPackageByName") {
                    return@mock realSearchService.getPackageByName(it.getArgument(0), it.getArgument(1))
                } else {
                    TODO()
                }
                Unit
            }
        )
        project.replaceService(VoqalSearchService::class.java, mockSearchService, testRootDisposable)

        project.scope.launch {
            val directive = mock<VoqalDirective>(
                defaultAnswer = {
                    if (it.method.name == "getIde") {
                        return@mock IdeContext(project)
                    } else {
                        TODO()
                    }
                    Unit
                }
            )
            val response = VoqalResponse(
                directive, listOf(
                    ToolCall.Function(
                        id = ToolId(MoveClassTool.NAME),
                        function = FunctionCall(
                            nameOrNull = MoveClassTool.NAME,
                            argumentsOrNull = JsonObject()
                                .put("class_name", "com.example.Test")
                                .put("new_package", "com.test2")
                                .toString()
                        )
                    )
                )
            )
            assertEquals(1, response.toolCalls.size)
            val toolCall = response.toolCalls[0] as ToolCall.Function
            val functionCall = toolCall.function
            assertEquals(MoveClassTool.NAME, functionCall.name)

            val moveClassTool = MoveClassTool()
            moveClassTool.actionPerformed(JsonObject(functionCall.arguments), directive)

            val oldLocation = realSearchService.findFile("com.example.Test")
            val newLocation = realSearchService.findFile("com.test2.Test")
            testContext.verify {
                assertNull(oldLocation)
                assertNotNull(newLocation)
                assertEquals("/src/com/test2/Test.kt", newLocation!!.path)
            }
            testContext.completeNow()
        }
        errorOnTimeout(testContext)
    }
}
