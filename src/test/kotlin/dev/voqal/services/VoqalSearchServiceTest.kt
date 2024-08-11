package dev.voqal.services

import com.intellij.openapi.components.service
import dev.voqal.JBTest
import kotlinx.coroutines.runBlocking

class VoqalSearchServiceTest : JBTest() {

    fun `testGet filenames`(): Unit = runBlocking {
        myFixture.addFileToProject("C.java", "class C {}")
        val searchService = project.service<VoqalSearchService>()
        val allFilesByExt = searchService.getAllFiles()
        assertEquals(1, allFilesByExt.size)
        assertEquals("/src/C.java", allFilesByExt.first().path)
    }

    fun `testGet all filenames`(): Unit = runBlocking {
        myFixture.addFileToProject("A.java", "public class A {}")
        myFixture.addFileToProject("B.kt", "class B {}")
        myFixture.addFileToProject("C.groovy", "class C {}")
        myFixture.addFileToProject("D.js", "class D {}")

        val searchService = project.service<VoqalSearchService>()
        val allFiles = searchService.getAllFiles()
        assertEquals(4, allFiles.size)
    }

    fun `test exact match on get closest match`(): Unit = runBlocking {
        myFixture.addFileToProject("python/test.py", "")
        myFixture.addFileToProject("test.md", "class B {}")

        val searchService = project.service<VoqalSearchService>()
        val match = searchService.findFile("python/test.py")
        assertTrue(match!!.path.endsWith("test.py"))
    }

    fun `test find class by fully qualified name`(): Unit = runBlocking {
        myFixture.addFileToProject("main/java/comExample.xml", "")
        myFixture.addFileToProject("com/example/Test.java", "")
        myFixture.addFileToProject("com/test2/Other.java", "")

        val searchService = project.service<VoqalSearchService>()
        val match = searchService.findFile("com.example.Test")
        assertTrue(match!!.path.endsWith("Test.java"))
    }
}
