package com.github.unclepomedev.ronassist.parser

import com.intellij.testFramework.ParsingTestCase

abstract class RonParsingTestCaseBase : ParsingTestCase("parser", "ron", RonParserDefinition()) {
    override fun getTestDataPath(): String = "src/test/testData"
    override fun skipSpaces(): Boolean = true
    override fun includeRanges(): Boolean = false
}
