package com.github.unclepomedev.ronassist.formatting

class RonFormatterTest : RonFormatterTestCaseBase() {
    fun testSpacing() = doTest()
    fun testIndentation() = doTest()
    fun testBlankLinesCompression() = doTest()
    fun testPosixNewline() = doTest()
    fun testSomeWithStruct() = doTest()
    fun testNoneWithStruct() = doTest()
    fun testIncompleteStructEntry() = doTest()
    fun testIncompleteMapEntry() = doTest()
}
