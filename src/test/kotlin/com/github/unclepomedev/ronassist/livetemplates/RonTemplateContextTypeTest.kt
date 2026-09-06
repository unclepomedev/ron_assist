package com.github.unclepomedev.ronassist.livetemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonTemplateContextTypeTest : BasePlatformTestCase() {

    private val contextType = RonTemplateContextType()

    fun testIsInContextForRonFile() {
        val file: PsiFile = myFixture.configureByText("test.ron", "(a: 1)")
        val actionContext = TemplateActionContext.create(file, myFixture.editor, 0, 0, false)
        assertTrue(contextType.isInContext(actionContext))
    }

    fun testIsNotInContextForOtherFile() {
        val file: PsiFile = myFixture.configureByText("test.txt", "hello")
        val actionContext = TemplateActionContext.create(file, myFixture.editor, 0, 0, false)
        assertFalse(contextType.isInContext(actionContext))
    }
}
