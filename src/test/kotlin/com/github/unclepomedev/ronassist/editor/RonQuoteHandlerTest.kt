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
        myFixture.type('\b') // Backspace
        myFixture.checkResult("(value: <caret>)")
    }

    // TODO: Move to gold files if multi-line test cases grow.

    fun testQuoteCompletionBetweenExistingStrings() {
        myFixture.configureByText(
            "test.ron",
            """
            {
                "name": "Reimu",
                <caret>
                "score": 100,
            }
            """
                .trimIndent(),
        )
        myFixture.type('"')
        myFixture.checkResult(
            """
            {
                "name": "Reimu",
                "<caret>"
                "score": 100,
            }
            """
                .trimIndent()
        )
    }

    fun testQuoteCompletionAfterIncompleteString() {
        myFixture.configureByText(
            "test.ron",
            """
            (
                value: "incomplete
                <caret>
            )
            """
                .trimIndent(),
        )
        myFixture.type('"')
        myFixture.checkResult(
            """
            (
                value: "incomplete
                "<caret>"
            )
            """
                .trimIndent()
        )
    }
}
