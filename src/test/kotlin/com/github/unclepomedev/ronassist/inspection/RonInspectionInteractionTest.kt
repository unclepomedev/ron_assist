package com.github.unclepomedev.ronassist.inspection

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonInspectionInteractionTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/inspection/interaction"

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            RonDuplicateMapKeyInspection::class.java,
            RonDuplicateStructFieldInspection::class.java,
        )
    }

    fun testBothInspectionsActiveSimultaneously() = doTest()

    private fun doTest() {
        val name = getTestName(true)
        val snakeName = name.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
        myFixture.testHighlighting("${snakeName}.ron")
    }
}
