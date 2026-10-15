package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.ide.util.treeView.smartTree.Sorter
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

    fun testAlphabeticalSorting() = doTest(sorted = true)

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
        assertEquals("player(...)", structElement.alphaSortKey)
        assertEquals("r#Player(...)", structElement.presentableText)

        val entry = PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java).first()
        val entryElement = RonStructureViewElement(entry)
        assertEquals("type", entryElement.alphaSortKey)
        assertEquals("r#type", entryElement.presentableText)
        assertEquals("= 42", entryElement.locationString)
    }

    fun testAlphaSortKeyRemovesRawIdentifierPrefix() {
        myFixture.configureByText("test.ron", """(r#type: 1, target: 2, r#zoo: 3, apple: 4)""")
        val file = myFixture.file
        val entries = PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java).toList()
        assertEquals(4, entries.size)

        val typeElement = RonStructureViewElement(entries[0])
        val targetElement = RonStructureViewElement(entries[1])
        val zooElement = RonStructureViewElement(entries[2])
        val appleElement = RonStructureViewElement(entries[3])

        assertEquals("type", typeElement.alphaSortKey)
        assertEquals("target", targetElement.alphaSortKey)
        assertEquals("zoo", zooElement.alphaSortKey)
        assertEquals("apple", appleElement.alphaSortKey)

        assertTrue(typeElement.alphaSortKey > targetElement.alphaSortKey)
        assertTrue(zooElement.alphaSortKey > typeElement.alphaSortKey)
        assertTrue(typeElement.alphaSortKey > appleElement.alphaSortKey)
    }

    fun testAlphaSortKeyHandlesRawStringsAndByteStringsInMapKeys() {
        myFixture.configureByText(
            "test.ron",
            """{
                r#"zoo"#: 1,
                r"apple": 2,
                br#"banana"#: 3,
                b"cherry": 4,
                "date": 5,
                r#extra: 6,
                42: 7,
                true: 8,
            }""",
        )
        val file = myFixture.file
        val entries = PsiTreeUtil.findChildrenOfType(file, RonMapEntry::class.java).toList()
        assertEquals(8, entries.size)

        val zooElement = RonStructureViewElement(entries[0])
        val appleElement = RonStructureViewElement(entries[1])
        val bananaElement = RonStructureViewElement(entries[2])
        val cherryElement = RonStructureViewElement(entries[3])
        val dateElement = RonStructureViewElement(entries[4])
        val extraElement = RonStructureViewElement(entries[5])
        val intElement = RonStructureViewElement(entries[6])
        val boolElement = RonStructureViewElement(entries[7])

        assertEquals("zoo", zooElement.alphaSortKey)
        assertEquals("apple", appleElement.alphaSortKey)
        assertEquals("banana", bananaElement.alphaSortKey)
        assertEquals("cherry", cherryElement.alphaSortKey)
        assertEquals("date", dateElement.alphaSortKey)
        assertEquals("extra", extraElement.alphaSortKey)
        assertEquals("42", intElement.alphaSortKey)
        assertEquals("true", boolElement.alphaSortKey)

        assertEquals("r#\"zoo\"#", zooElement.presentableText)
        assertEquals("r\"apple\"", appleElement.presentableText)
        assertEquals("br#\"banana\"#", bananaElement.presentableText)
        assertEquals("b\"cherry\"", cherryElement.presentableText)
        assertEquals("\"date\"", dateElement.presentableText)
        assertEquals("r#extra", extraElement.presentableText)
        assertEquals("42", intElement.presentableText)
        assertEquals("true", boolElement.presentableText)
    }

    private fun doTest(sorted: Boolean = false) {
        val testName = snakeCase(getTestName(true))
        myFixture.configureByFile("$testName.ron")
        val expected = File(testDataPath, "$testName.txt").readText().trim()
        myFixture.testStructureView { view ->
            if (sorted) {
                view.setActionActive(Sorter.ALPHA_SORTER.name, true)
            }
            PlatformTestUtil.expandAll(view.tree)
            PlatformTestUtil.assertTreeEqual(view.tree, expected)
        }
    }

    private fun snakeCase(camel: String): String =
        camel.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
