package com.github.unclepomedev.ronassist.inspection

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonDuplicateMapKeyInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/inspection/duplicate_map_key"

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RonDuplicateMapKeyInspection::class.java)
    }

    fun testBasicDuplicate() = doTest()

    fun testThreeDuplicates() = doTest()

    fun testNumericKey() = doTest()

    fun testNoDuplicate() = doTest()

    fun testCaseSensitive() = doTest()

    fun testNestedMapsIndependent() = doTest()

    fun testEmptyMap() = doTest()

    fun testSingleEntry() = doTest()

    fun testStructKeyDuplicate() = doTest()

    fun testStructKeyDifferentContent() = doTest()

    fun testStringAndRawString() = doTest()

    fun testStandardStringFollowedByRawString() = doTest()

    fun testStringAndNumberAreDistinct() = doTest()

    private fun doTest() {
        val name = getTestName(true)
        val snakeName = name.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
        myFixture.testHighlighting("${snakeName}.ron")
    }
}
