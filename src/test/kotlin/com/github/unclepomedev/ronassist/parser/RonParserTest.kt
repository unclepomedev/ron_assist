package com.github.unclepomedev.ronassist.parser

class RonParserTest : RonParsingTestCaseBase() {
    fun testPrimitives() = doTest(true)
    fun testCollections() = doTest(true)
    fun testStructsAndTuples() = doTest(true)
    fun testComplexConfig() = doTest(true)
}
