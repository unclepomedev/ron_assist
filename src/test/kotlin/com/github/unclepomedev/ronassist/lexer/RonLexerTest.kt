package com.github.unclepomedev.ronassist.lexer

import com.intellij.lexer.Lexer
import com.intellij.testFramework.LexerTestCase

class RonLexerTest : LexerTestCase() {
    override fun createLexer(): Lexer = RonLexerAdapter()
    override fun getDirPath(): String = "src/test/testData/lexer"

    fun testBasicKeywords() {
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

    fun testComments() {
        doTest(
            "// line\n/* block */ /**/ /***/",
            "RonTokenType.LINE_COMMENT ('// line')\n" +
                    "WHITE_SPACE ('\\n')\n" +
                    "RonTokenType.BLOCK_COMMENT ('/* block */')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.BLOCK_COMMENT ('/**/')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.BLOCK_COMMENT ('/***/')"
        )
    }

    fun testStringsAndChars() {
        doTest(
            "\"text\" r#\"raw\"# 'a'",
            "RonTokenType.STRING ('\"text\"')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.RAW_STRING ('r#\"raw\"#')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.CHAR (''a'')"
        )
    }

    fun testBooleansAndOptions() {
        doTest(
            "true false Some None",
            "RonTokenType.true ('true')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.false ('false')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.Some ('Some')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.None ('None')"
        )
    }

    fun testNumbers() {
        doTest(
            "3.14f32 -0xFF 0b1010",
            "RonTokenType.FLOAT ('3.14f32')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.INTEGER ('-0xFF')\n" +
                    "WHITE_SPACE (' ')\n" +
                    "RonTokenType.INTEGER ('0b1010')"
        )
    }
}
