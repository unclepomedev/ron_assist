package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.TokenType
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class RonSyntaxHighlighterTest {

    private val highlighter = RonSyntaxHighlighter()

    @Test
    fun testKeywords() {
        val expected = arrayOf(RonSyntaxHighlighter.KEYWORD)
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.TRUE))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.FALSE))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.SOME))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.NONE))
    }

    @Test
    fun testNumbers() {
        val expected = arrayOf(RonSyntaxHighlighter.NUMBER)
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.FLOAT))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.INTEGER))
    }

    @Test
    fun testStringsAndChars() {
        val expected = arrayOf(RonSyntaxHighlighter.STRING)
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.STRING))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.RAW_STRING))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.CHAR))
    }

    @Test
    fun testComments() {
        assertArrayEquals(
            arrayOf(RonSyntaxHighlighter.LINE_COMMENT),
            highlighter.getTokenHighlights(RonTypes.LINE_COMMENT)
        )
        assertArrayEquals(
            arrayOf(RonSyntaxHighlighter.BLOCK_COMMENT),
            highlighter.getTokenHighlights(RonTypes.BLOCK_COMMENT)
        )
    }

    @Test
    fun testIdentifiers() {
        val expected = arrayOf(RonSyntaxHighlighter.IDENTIFIER)
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.IDENTIFIER))
    }

    @Test
    fun testBracesAndBrackets() {
        assertArrayEquals(arrayOf(RonSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(RonTypes.LBRACE))
        assertArrayEquals(arrayOf(RonSyntaxHighlighter.BRACES), highlighter.getTokenHighlights(RonTypes.RBRACE))

        assertArrayEquals(arrayOf(RonSyntaxHighlighter.BRACKETS), highlighter.getTokenHighlights(RonTypes.LBRACK))
        assertArrayEquals(arrayOf(RonSyntaxHighlighter.BRACKETS), highlighter.getTokenHighlights(RonTypes.RBRACK))

        assertArrayEquals(arrayOf(RonSyntaxHighlighter.PARENTHESES), highlighter.getTokenHighlights(RonTypes.LPAREN))
        assertArrayEquals(arrayOf(RonSyntaxHighlighter.PARENTHESES), highlighter.getTokenHighlights(RonTypes.RPAREN))
    }

    @Test
    fun testPunctuation() {
        val expected = arrayOf(RonSyntaxHighlighter.COMMA)
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.COMMA))
    }

    @Test
    fun testBadCharacter() {
        val expected = arrayOf(RonSyntaxHighlighter.BAD_CHARACTER)
        assertArrayEquals(expected, highlighter.getTokenHighlights(TokenType.BAD_CHARACTER))
    }

    @Test
    fun testUnmappedTokensReturnEmptyArray() {
        val expected = emptyArray<TextAttributesKey>()

        // Ensures that composite nodes (AST branches) that are not to be colored, and undefined symbols (such as colons), will return an empty array without crashing.
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.COLON))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.VALUE))
        assertArrayEquals(expected, highlighter.getTokenHighlights(RonTypes.MAP_ENTRY))
        assertArrayEquals(expected, highlighter.getTokenHighlights(TokenType.WHITE_SPACE))
    }
}
