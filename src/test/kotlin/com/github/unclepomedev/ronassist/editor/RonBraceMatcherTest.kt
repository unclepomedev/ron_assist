package com.github.unclepomedev.ronassist.editor

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonBraceMatcherTest : BasePlatformTestCase() {

    fun testParenCompletion() {
        myFixture.configureByText("test.ron", "Player<caret>")
        myFixture.type('(')
        myFixture.checkResult("Player(<caret>)")
    }

    fun testBraceCompletion() {
        myFixture.configureByText("test.ron", "<caret>")
        myFixture.type('{')
        myFixture.checkResult("{<caret>}")
    }

    fun testBracketCompletion() {
        myFixture.configureByText("test.ron", "<caret>")
        myFixture.type('[')
        myFixture.checkResult("[<caret>]")
    }

    fun testBackspaceDeletesPairedBrace() {
        myFixture.configureByText("test.ron", "(<caret>)")
        myFixture.type('\b')
        myFixture.checkResult("<caret>")
    }
}
