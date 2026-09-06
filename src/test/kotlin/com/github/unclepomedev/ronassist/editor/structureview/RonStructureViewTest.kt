package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.psi.RonMapEntry
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
