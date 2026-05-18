package com.github.unclepomedev.ronassist.formatting

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

abstract class RonFormatterTestCaseBase : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/formatter"

    protected fun doTest(extension: String = "ron") {
        val testName = getTestName(false)
        myFixture.configureByFile("$testName/before.$extension")
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(myFixture.file)
        }
        myFixture.checkResultByFile("$testName/after.$extension")
    }
}
