package com.github.unclepomedev.ronassist.parser

class RonParserTest : RonParsingTestCaseBase() {
    fun testPrimitives() = doTest(true)
    fun testNumbersWithUnderscores() = doTest(true)
    fun testCollections() = doTest(true)
    fun testStructsAndTuples() = doTest(true)
    fun testComplexConfig() = doTest(true)
    fun testDeeplyNested() = doTest(true)
    fun testIncompleteEntries() = doTest(true)
    fun testAllTokens() = doTest(true)
    fun testIncompleteStructEntry() = doTest(true)
    fun testIncompleteMapEntry() = doTest(true)
    fun testIncompleteEntriesWithCommas() = doTest(true)
}
