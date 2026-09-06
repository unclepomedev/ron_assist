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
                "RonTokenType.) (')')",
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
                "RonTokenType.BLOCK_COMMENT ('/***/')",
        )
    }

    fun testStringsAndChars() {
        doTest(
            "\"text\" r#\"raw\"# 'a'",
            "RonTokenType.STRING ('\"text\"')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.RAW_STRING ('r#\"raw\"#')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.CHAR (''a'')",
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
                "RonTokenType.None ('None')",
        )
    }

    fun testNumbers() {
        doTest(
            "3.14f32 -0xFF 0b1010",
            "RonTokenType.FLOAT ('3.14f32')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('-0xFF')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('0b1010')",
        )
    }

    fun testNumbersWithUnderscoreSeparators() {
        doTest(
            "1_000 6_000_000 1_000.000_5 0xFF_FF 0b1010_1010",
            "RonTokenType.INTEGER ('1_000')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('6_000_000')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.FLOAT ('1_000.000_5')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('0xFF_FF')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('0b1010_1010')",
        )
    }

    fun testMalformedUnderscoreNumbers() {
        doTest(
            "1__2 1_ 0x_FF",
            "RonTokenType.INTEGER ('1')\n" +
                "RonTokenType.IDENTIFIER ('__2')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('1')\n" +
                "RonTokenType.IDENTIFIER ('_')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('0')\n" +
                "RonTokenType.IDENTIFIER ('x_FF')",
        )
    }

    fun testIncompleteString() {
        doTest(
            "\"hello",
            "RonTokenType.STRING ('\"hello')",
        )
    }

    fun testIncompleteChar() {
        doTest(
            "'a",
            "RonTokenType.CHAR (''a')",
        )
    }

    fun testJustOpeningQuote() {
        doTest(
            "\"",
            "RonTokenType.STRING ('\"')",
        )
    }

    fun testRawStringVariants() {
        doTest(
            "r\"simple\" r#\"hash\"# r##\"double_hash\"##",
            """
            RonTokenType.RAW_STRING ('r"simple"')
            WHITE_SPACE (' ')
            RonTokenType.RAW_STRING ('r#"hash"#')
            WHITE_SPACE (' ')
            RonTokenType.RAW_STRING ('r##"double_hash"##')
            """
                .trimIndent(),
        )
    }

    fun testRawStringWithNewline() {
        doTest(
            "r#\"line1\nline2\"#",
            "RonTokenType.RAW_STRING ('r#\"line1\\nline2\"#')",
        )
    }

    fun testBadCharacter() {
        doTest(
            "@",
            "BAD_CHARACTER ('@')",
        )
    }

    fun testIncompleteHashedRawString() {
        doTest(
            "r#\"hello",
            "RonTokenType.RAW_STRING ('r#\"hello')",
        )
    }

    fun testIncompleteDoubleHashedRawString() {
        doTest(
            "r##\"hello",
            "RonTokenType.RAW_STRING ('r##\"hello')",
        )
    }

    fun testStringDoesNotSpanLines() {
        doTest(
            "\"foo\n\"bar\"",
            "RonTokenType.STRING ('\"foo')\n" +
                "WHITE_SPACE ('\\n')\n" +
                "RonTokenType.STRING ('\"bar\"')",
        )
    }

    fun testIncompleteStringStopsAtNewline() {
        doTest(
            "\"unclosed\n42",
            "RonTokenType.STRING ('\"unclosed')\n" +
                "WHITE_SPACE ('\\n')\n" +
                "RonTokenType.INTEGER ('42')",
        )
    }

    fun testCharDoesNotSpanLines() {
        // The trailing single `'` is recognized as its own (empty/incomplete)
        // CHAR token by the permissive lexer, which is intentional.
        doTest(
            "'a\n'",
            "RonTokenType.CHAR (''a')\n" + "WHITE_SPACE ('\\n')\n" + "RonTokenType.CHAR (''')",
        )
    }

    fun testExponentFloatsAndOctalNumbers() {
        doTest(
            "1.5e-3 -2.0e5 0o77_77 128u128 64i64 10usize",
            "RonTokenType.FLOAT ('1.5e-3')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.FLOAT ('-2.0e5')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('0o77_77')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('128u128')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('64i64')\n" +
                "WHITE_SPACE (' ')\n" +
                "RonTokenType.INTEGER ('10usize')",
        )
    }

    fun testRawStringWithInnerQuotes() {
        doTest(
            "r#\"He said \"hello\"\"#",
            "RonTokenType.RAW_STRING ('r#\"He said \"hello\"\"#')",
        )
    }
}
