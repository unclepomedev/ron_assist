package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.lexer.RonLexerAdapter
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class RonSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val KEYWORD = createTextAttributesKey("RON_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val NUMBER = createTextAttributesKey("RON_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING = createTextAttributesKey("RON_STRING", DefaultLanguageHighlighterColors.STRING)
        val LINE_COMMENT = createTextAttributesKey("RON_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BLOCK_COMMENT = createTextAttributesKey("RON_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val IDENTIFIER = createTextAttributesKey("RON_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val BRACES = createTextAttributesKey("RON_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val BRACKETS = createTextAttributesKey("RON_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val PARENTHESES = createTextAttributesKey("RON_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val COMMA = createTextAttributesKey("RON_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val BAD_CHARACTER = createTextAttributesKey("RON_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val STRING_KEYS = arrayOf(STRING)
        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        private val BLOCK_COMMENT_KEYS = arrayOf(BLOCK_COMMENT)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        private val BRACES_KEYS = arrayOf(BRACES)
        private val BRACKETS_KEYS = arrayOf(BRACKETS)
        private val PARENTHESES_KEYS = arrayOf(PARENTHESES)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = RonLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            RonTypes.TRUE, RonTypes.FALSE, RonTypes.SOME, RonTypes.NONE -> KEYWORD_KEYS
            RonTypes.FLOAT, RonTypes.INTEGER -> NUMBER_KEYS
            RonTypes.STRING, RonTypes.RAW_STRING, RonTypes.CHAR -> STRING_KEYS
            RonTypes.LINE_COMMENT -> LINE_COMMENT_KEYS
            RonTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEYS
            RonTypes.IDENTIFIER -> IDENTIFIER_KEYS
            RonTypes.LBRACE, RonTypes.RBRACE -> BRACES_KEYS
            RonTypes.LBRACK, RonTypes.RBRACK -> BRACKETS_KEYS
            RonTypes.LPAREN, RonTypes.RPAREN -> PARENTHESES_KEYS
            RonTypes.COMMA -> COMMA_KEYS
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> EMPTY_KEYS
        }
    }
}
