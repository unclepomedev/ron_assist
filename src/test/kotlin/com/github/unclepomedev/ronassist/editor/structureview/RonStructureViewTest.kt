package com.github.unclepomedev.ronassist.editor.structureview

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
