package com.github.unclepomedev.ronassist.editor

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonSmartEnterProcessorTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/smart_enter"

    fun testCompletesEntryWithoutComma() = doTest()
    fun testDoesNotDuplicateExistingComma() = doTest()
    fun testCompletesFromInsideString() = doTest()
    fun testCompletesMapEntry() = doTest()
    fun testCompletesNestedStruct() = doTest()
    fun testNoCompletesFromInsideString() = doTest()
    fun testNoCompletesNestedStruct() = doTest()

    private fun doTest() {
        val testName = getTestName(true)
        val snakeName = testName.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
        myFixture.configureByFile("$snakeName/before.ron")
        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT)
        myFixture.checkResultByFile("$snakeName/after.ron")
    }
}
