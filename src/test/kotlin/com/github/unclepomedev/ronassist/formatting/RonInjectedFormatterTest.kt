package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.codeStyle.CodeStyleManager

class RonInjectedFormatterTest : RonFormatterTestCaseBase() {
    fun testRonInMarkdown() = doInjectedTest()
    fun testRonInMarkdownIndented() = doInjectedTest()

    private fun doInjectedTest() {
        val testName = getTestName(false)
        myFixture.configureByFile("$testName/before.md")

        // Guard against false positives: if Markdown injection is not active in the test
        // environment, this test would silently pass without exercising the injected formatter.
        val injectedRonFilesBefore = collectInjectedRonFiles(myFixture.file)
        assertFalse(
            "Expected RON to be injected into the Markdown fenced code block.", injectedRonFilesBefore.isEmpty()
        )
        val injectedTextsBefore = injectedRonFilesBefore.map { it.text }

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(myFixture.file)
        }

        myFixture.checkResultByFile("$testName/after.md")

        // Host equality alone is not sufficient — assert the injected fragment text itself
        // is unchanged, so a failure points directly at the injected formatter path.
        val injectedTextsAfter = collectInjectedRonFiles(myFixture.file).map { it.text }
        assertEquals(
            "Injected RON fragments must not be modified by the formatter.", injectedTextsBefore, injectedTextsAfter
        )
    }

    private fun collectInjectedRonFiles(host: PsiFile): List<PsiFile> {
        val injectionManager = InjectedLanguageManager.getInstance(host.project)
        val result = mutableListOf<PsiFile>()
        host.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                injectionManager.enumerate(element) { injectedPsi, _ ->
                    if (injectedPsi.language == RonLanguage.INSTANCE) {
                        result.add(injectedPsi)
                    }
                }
                super.visitElement(element)
            }
        })
        return result
    }
}
