package com.github.unclepomedev.ronassist.parser

import com.intellij.testFramework.ParsingTestCase

class RonParsingTest : ParsingTestCase("parser", "ron", RonParserDefinition()) {
    override fun getTestDataPath(): String = "src/test/testData"
    override fun skipSpaces(): Boolean = true
    override fun includeRanges(): Boolean = true

    fun testHelloWorld() {
        doTest(true)
    }
}
