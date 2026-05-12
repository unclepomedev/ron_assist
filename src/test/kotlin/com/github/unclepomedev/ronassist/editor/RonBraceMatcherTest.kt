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

    fun testParenNotInsertedInsideString() {
        myFixture.configureByText("test.ron", "(value: \"hello <caret>world\")")
        myFixture.type('(')
        myFixture.checkResult("(value: \"hello (<caret>world\")")
    }

    fun testBraceNotInsertedInsideString() {
        myFixture.configureByText("test.ron", "(value: \"<caret>\")")
        myFixture.type('{')
        myFixture.checkResult("(value: \"{<caret>\")")
    }

    fun testBracketNotInsertedInsideComment() {
        myFixture.configureByText("test.ron", "// comment <caret>\n(x: 1)")
        myFixture.type('[')
        myFixture.checkResult("// comment [<caret>\n(x: 1)")
    }

    fun testQuoteSkipsExistingRawStringClosingQuote() {
        myFixture.configureByText("test.ron", "(value: r\"hello<caret>\")")
        myFixture.type('"')
        myFixture.checkResult("(value: r\"hello\"<caret>)")
    }
}
