package com.github.unclepomedev.ronassist.editor.folding

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonFoldingBuilderTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/folding"

    override fun setUp() {
        super.setUp()
        RonFoldingSettings.instance.apply {
            collapseMaps = false
            collapseLists = false
            collapseStructs = false
            collapseBlockComments = false
        }
    }
    fun testMapBasic() = doTest()
    fun testListBasic() = doTest()
    fun testStructNamed() = doTest()
    fun testTupleAnonymous() = doTest()
    fun testNestedAllKinds() = doTest()
    fun testSingleLineExcluded() = doTest()
    fun testEmptyCollections() = doTest()
    fun testBlockComment() = doTest()
    fun testCustomRegion() = doTest()
    fun testMixedContent() = doTest()
    fun testErrorRecovery() = doTest()
    fun testCollapsedByDefaultMaps() {
        RonFoldingSettings.instance.collapseMaps = true
        doTest()
    }

    private fun doTest() {
        val name = getTestName(true)
        myFixture.testFolding("${testDataPath}/${snakeCase(name)}.ron")
    }

    private fun snakeCase(camel: String): String =
        camel.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
