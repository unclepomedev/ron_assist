package com.github.unclepomedev.ronassist.editor.folding

import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonFoldingBuilderTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/folding"

    private data class SettingsSnapshot(
        val collapseMaps: Boolean,
        val collapseLists: Boolean,
        val collapseStructs: Boolean,
        val collapseBlockComments: Boolean,
    )

    private lateinit var savedSettings: SettingsSnapshot

    override fun setUp() {
        super.setUp()
        val s = RonFoldingSettings.instance
        savedSettings = SettingsSnapshot(
            s.collapseMaps,
            s.collapseLists,
            s.collapseStructs,
            s.collapseBlockComments,
        )
        // Start each test from a known neutral state.
        s.collapseMaps = false
        s.collapseLists = false
        s.collapseStructs = false
        s.collapseBlockComments = false
    }

    override fun tearDown() {
        try {
            val s = RonFoldingSettings.instance
            s.collapseMaps = savedSettings.collapseMaps
            s.collapseLists = savedSettings.collapseLists
            s.collapseStructs = savedSettings.collapseStructs
            s.collapseBlockComments = savedSettings.collapseBlockComments
        } finally {
            super.tearDown()
        }
    }

    fun testMapBasic() = doFoldingTest()
    fun testListBasic() = doFoldingTest()
    fun testStructNamed() = doFoldingTest()
    fun testTupleAnonymous() = doFoldingTest()
    fun testNestedAllKinds() = doFoldingTest()
    fun testSingleLineExcluded() = doFoldingTest()
    fun testEmptyCollections() = doFoldingTest()
    fun testBlockComment() = doFoldingTest()
    fun testCustomRegion() = doFoldingTest()
    fun testMixedContent() = doFoldingTest()
    fun testErrorRecovery() = doFoldingTest()

    // Because testFoldingWithCollapseStatus behaves unexpectedly,
    // verifications below are performed via API instead of using a file.
    fun testCollapsedByDefaultMaps() {
        RonFoldingSettings.instance.collapseMaps = true
        assertCollapsedByDefault<RonMap>(
            """
                {
                    "key1": "value1",
                    "key2": "value2",
                }
            """.trimIndent(),
            expected = true,
        )
    }

    fun testCollapsedByDefaultMapsOff() {
        assertCollapsedByDefault<RonMap>(
            """
                {
                    "key": "value",
                }
            """.trimIndent(),
            expected = false,
        )
    }

    fun testCollapsedByDefaultLists() {
        RonFoldingSettings.instance.collapseLists = true
        assertCollapsedByDefault<RonList>(
            """
                [
                    1,
                    2,
                ]
            """.trimIndent(),
            expected = true,
        )
    }

    fun testCollapsedByDefaultStructs() {
        RonFoldingSettings.instance.collapseStructs = true
        assertCollapsedByDefault<RonStructOrTuple>(
            """
                Player(
                    name: "Reimu",
                )
            """.trimIndent(),
            expected = true,
        )
    }

    /**
     * Verifies that mutations from prior tests do not leak into this one,
     * ensuring setUp/tearDown correctly restore singleton state.
     */
    fun testSettingsResetBetweenTests() {
        val s = RonFoldingSettings.instance
        assertFalse("collapseMaps should be false at test start", s.collapseMaps)
        assertFalse("collapseLists should be false at test start", s.collapseLists)
        assertFalse("collapseStructs should be false at test start", s.collapseStructs)
        assertFalse("collapseBlockComments should be false at test start", s.collapseBlockComments)
    }

    private fun doFoldingTest() {
        val name = getTestName(true)
        myFixture.testFolding("${testDataPath}/${snakeCase(name)}.ron")
    }

    private inline fun <reified T : PsiElement> assertCollapsedByDefault(
        content: String,
        expected: Boolean,
    ) {
        myFixture.configureByText("test.ron", content)
        val builder = RonFoldingBuilder()
        val descriptors = builder.buildFoldRegions(myFixture.file.node, myFixture.editor.document)
        val descriptor = descriptors.find { it.element.psi is T }
            ?: error("${T::class.simpleName} should produce a fold descriptor")
        assertEquals(
            "${T::class.simpleName} collapsedByDefault mismatch",
            expected,
            builder.isCollapsedByDefault(descriptor.element),
        )
    }

    private fun snakeCase(camel: String): String =
        camel.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
