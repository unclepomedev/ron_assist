package com.github.unclepomedev.ronassist.formatting

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

abstract class RonFormatterTestCaseBase : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/formatter"

    protected fun doTest() {
        val testName = getTestName(false)
        myFixture.configureByFile("$testName/before.ron")
        val selectionModel = myFixture.editor.selectionModel
        WriteCommandAction.runWriteCommandAction(project) {
            if (selectionModel.hasSelection()) {
                CodeStyleManager.getInstance(project).reformatText(
                    myFixture.file,
                    selectionModel.selectionStart,
                    selectionModel.selectionEnd
                )
            } else {
                CodeStyleManager.getInstance(project).reformat(myFixture.file)
            }
        }
        myFixture.checkResultByFile("$testName/after.ron")
    }

    protected fun doTestWithTrailingComma(enabled: Boolean) {
        val settings = CodeStyle.createTestSettings(CodeStyle.getSettings(project))
        settings.getCustomSettings(RonCodeStyleSettings::class.java).addTrailingComma = enabled
        CodeStyle.doWithTemporarySettings(project, settings, Runnable { doTest() })
    }
}
