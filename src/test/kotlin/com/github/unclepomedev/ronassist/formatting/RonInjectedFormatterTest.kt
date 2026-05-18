package com.github.unclepomedev.ronassist.formatting

class RonInjectedFormatterTest : RonFormatterTestCaseBase() {
    fun testRonInMarkdown() = doTest(extension = "md")
}
