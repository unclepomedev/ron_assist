package com.github.unclepomedev.ronassist.parser

class RonParserTest : RonParsingTestCaseBase() {
    fun testPrimitives() = doTest(true)

    fun testNumbersWithUnderscores() = doTest(true)

    fun testMalformedUnderscoreNumbers() = doTest(true)

    fun testCollections() = doTest(true)

    fun testStructsAndTuples() = doTest(true)

    fun testComplexConfig() = doTest(true)

    fun testDeeplyNested() = doTest(true)

    fun testIncompleteEntries() = doTest(true)

    fun testAllTokens() = doTest(true)

    fun testIncompleteStructEntry() = doTest(true)

    fun testIncompleteMapEntry() = doTest(true)

    fun testIncompleteEntriesWithCommas() = doTest(true)

    fun testExtensions() = doTest(true)

    fun testAttributeMultiple() = doTest(true)

    fun testAttributeMultipleExtensions() = doTest(true)

    fun testAttributeTrailingComma() = doTest(true)

    fun testAttributeEmptyList() = doTest(true)

    fun testAttributeComments() = doTest(true)

    fun testAttributeMissingClosingParen() = doTest(true)

    fun testAttributeMissingValue() = doTest(true)

    fun testAttributeRecovery() = doTest(true)

    fun testExtendedFloats() = doTest(true)

    fun testIncompleteFloats() = doTest(true)
}
