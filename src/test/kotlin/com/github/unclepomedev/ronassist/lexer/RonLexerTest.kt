package com.github.unclepomedev.ronassist.lexer

import com.intellij.lexer.Lexer
import com.intellij.testFramework.LexerTestCase

class RonLexerTest : LexerTestCase() {
    override fun createLexer(): Lexer = RonLexerAdapter()
    override fun getDirPath(): String = "src/test/testData/lexer"

    fun testHelloWorld() {
        doTest(
            "Struct( field: 42i32 )",
            "RonTokenType.IDENTIFIER ('Struct')\n" +
                    "RonTokenType.( ('(')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.IDENTIFIER ('field')\n" +
                    "RonTokenType.: (':')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.INTEGER ('42i32')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.) (')')"
        )
    }
}
