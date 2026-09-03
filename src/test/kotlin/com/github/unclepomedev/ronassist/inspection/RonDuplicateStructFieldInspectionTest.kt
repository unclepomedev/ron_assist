package com.github.unclepomedev.ronassist.inspection

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonDuplicateStructFieldInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/inspection/duplicate_struct_field"

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RonDuplicateStructFieldInspection::class.java)
    }

    fun testBasicDuplicate() = doTest()

    fun testThreeDuplicates() = doTest()

    fun testAnonymousTupleSkipped() = doTest()

    fun testAnonymousStructWithFields() = doTest()

    fun testNoDuplicate() = doTest()

    fun testNestedStructsIndependent() = doTest()

    fun testEmptyStruct() = doTest()

    fun testSingleField() = doTest()

    fun testEmptyTuple() = doTest()

    private fun doTest() {
        val name = getTestName(true)
        val snakeName = name.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
        myFixture.testHighlighting("${snakeName}.ron")
    }
}
