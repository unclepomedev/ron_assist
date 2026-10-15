package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

class RonStructureViewTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/structureview"

    fun testMapEntries() = doTest()

    fun testNamedStruct() = doTest()

    fun testList() = doTest()

    fun testAnonymousTupleAllPrimitives() = doTest()

    fun testPrimitiveRoot() = doTest()

    fun testEmptyMap() = doTest()

    fun testNestedMap() = doTest()

    fun testMixedNesting() = doTest()

    fun testListOfMaps() = doTest()

    fun testAnonymousTupleWithContainer() = doTest()

    fun testLeadingComment() = doTest()

    fun testMapEntryWithSpacesInKey() = doTest()

    fun testAlphaSortKeyRemovesSurroundingQuotes() {
        myFixture.configureByText("test.ron", """{"Alpha": 1, "beta": 2}""")
        val file = myFixture.file
        val entries =
            PsiTreeUtil.findChildrenOfType(
                    file,
                    RonMapEntry::class.java,
                )
                .toList()
        assertEquals(2, entries.size)
        val alphaElement = RonStructureViewElement(entries[0])
        val betaElement = RonStructureViewElement(entries[1])
        assertEquals("alpha", alphaElement.alphaSortKey)
        assertEquals("beta", betaElement.alphaSortKey)
    }

    fun testValidElementPresentationAndProperties() {
        myFixture.configureByText("test.ron", """{"key": 42}""")
        val file = myFixture.file
        val entry = PsiTreeUtil.findChildrenOfType(file, RonMapEntry::class.java).first()
        val viewElement = RonStructureViewElement(entry)
        assertEquals(entry, viewElement.element)
        assertEquals("key", viewElement.alphaSortKey)
        assertEquals("\"key\"", viewElement.presentableText)
        assertEquals("= 42", viewElement.locationString)
        assertNotNull(viewElement.getIcon(false))
    }

    fun testRawIdentifierPresentationAndProperties() {
        myFixture.configureByText("test.ron", """r#Player(r#type: 42)""")
        val file = myFixture.file
        val struct = PsiTreeUtil.findChildrenOfType(file, RonStructOrTuple::class.java).first()
        val structElement = RonStructureViewElement(struct)
        assertEquals("r#player(...)", structElement.alphaSortKey)
        assertEquals("r#Player(...)", structElement.presentableText)

        val entry = PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java).first()
        val entryElement = RonStructureViewElement(entry)
        assertEquals("r#type", entryElement.alphaSortKey)
        assertEquals("r#type", entryElement.presentableText)
        assertEquals("= 42", entryElement.locationString)
    }

    private fun doTest() {
        val testName = snakeCase(getTestName(true))
        myFixture.configureByFile("$testName.ron")
        val expected = File(testDataPath, "$testName.txt").readText().trim()
        myFixture.testStructureView { view ->
            PlatformTestUtil.expandAll(view.tree)
            PlatformTestUtil.assertTreeEqual(view.tree, expected)
        }
    }

    private fun snakeCase(camel: String): String =
        camel.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
