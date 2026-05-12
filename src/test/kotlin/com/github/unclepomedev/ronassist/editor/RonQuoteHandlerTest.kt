package com.github.unclepomedev.ronassist.editor

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonQuoteHandlerTest : BasePlatformTestCase() {

    fun testDoubleQuoteCompletion() {
        myFixture.configureByText("test.ron", "(value: <caret>)")
        myFixture.type('"')
        myFixture.checkResult("(value: \"<caret>\")")
    }

    fun testSingleQuoteCompletion() {
        myFixture.configureByText("test.ron", "(value: <caret>)")
        myFixture.type("'")
        myFixture.checkResult("(value: '<caret>')")
    }

    fun testQuoteSkipsExistingClosingQuote() {
        myFixture.configureByText("test.ron", "(value: \"hello<caret>\")")
        myFixture.type('"')
        myFixture.checkResult("(value: \"hello\"<caret>)")
    }

    fun testBackspaceDeletesPairedQuote() {
        myFixture.configureByText("test.ron", "(value: \"<caret>\")")
        myFixture.type('\b')  // Backspace
        myFixture.checkResult("(value: <caret>)")
    }
}
