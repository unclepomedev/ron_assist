package com.github.unclepomedev.ronassist.editor

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonCommenterTest : BasePlatformTestCase() {

    fun testLineComment() {
        myFixture.configureByText("test.ron", "<caret>(key: \"value\")")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.checkResult("//(key: \"value\")")
    }

    fun testLineUncomment() {
        myFixture.configureByText("test.ron", "<caret>//(key: \"value\")")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.checkResult("(key: \"value\")")
    }

    fun testBlockCommentSingleLine() {
        myFixture.configureByText("test.ron", "(key: <selection>\"value\"</selection>)")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_BLOCK)
        myFixture.checkResult("(key: /*\"value\"*/)")
    }

    fun testBlockUncommentSingleLine() {
        myFixture.configureByText("test.ron", "(key: <selection>/*\"value\"*/</selection>)")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_BLOCK)
        myFixture.checkResult("(key: \"value\")")
    }

    fun testMultiLineLineComment() {
        doTest()
    }

    fun testBlockCommentMultiLine() {
        doTest()
    }

    fun testEmptyLineComment() {
        myFixture.configureByText("test.ron", "<caret>\n")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.checkResult("//\n")
    }

    override fun getTestDataPath() = "src/test/testData/commenter"
    private fun doTest() {
        val textName = getTestName(false)
        myFixture.configureByFile("$textName/before.ron")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        myFixture.configureByFile("$textName/after.ron")
    }
}
