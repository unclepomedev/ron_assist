package com.github.unclepomedev.ronassist.editor

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonEnterBetweenBracesTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/testData/editor"

    fun testEnterBetweenBrackets() {
        myFixture.configureByFile("enter_between_brackets/before.ron")
        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_ENTER)
        myFixture.checkResultByFile("enter_between_brackets/after.ron")
    }
}
