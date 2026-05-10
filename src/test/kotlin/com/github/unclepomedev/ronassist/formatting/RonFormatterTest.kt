package com.github.unclepomedev.ronassist.formatting

class RonFormatterTest : RonFormatterTestCaseBase() {
    fun testSpacing() = doTest()
    fun testIndentation() = doTest()
    fun testBlankLinesCompression() = doTest()
    fun testPosixNewline() = doTest()
}
