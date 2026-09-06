package com.github.unclepomedev.ronassist.spellchecker

import com.github.unclepomedev.ronassist.psi.RonCharVal
import com.github.unclepomedev.ronassist.psi.RonStringVal
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.psi.PsiComment
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonSpellcheckingStrategyTest : BasePlatformTestCase() {

    private val strategy = RonSpellcheckingStrategy()

    fun testStringLiteralIsTokenized() {
        myFixture.configureByText("test.ron", """{"key": "hello world"}""")
        val string =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStringVal::class.java)
                ?: error("RonStringVal not found")
        assertIsNotEmptyTokenizer(strategy.getTokenizer(string))
    }

    fun testStructNameIsTokenized() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val struct =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        val identifier =
            struct.node.findChildByType(RonTypes.IDENTIFIER)?.psi ?: error("identifier not found")
        assertIsNotEmptyTokenizer(strategy.getTokenizer(identifier))
    }

    fun testStructEntryNameIsTokenized() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val entry =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructEntry::class.java)
                ?: error("entry not found")
        val identifier =
            entry.node.findChildByType(RonTypes.IDENTIFIER)?.psi ?: error("identifier not found")
        assertIsNotEmptyTokenizer(strategy.getTokenizer(identifier))
    }

    fun testCommentIsTokenized() {
        myFixture.configureByText("test.ron", "// hello world\n42")
        val comment =
            PsiTreeUtil.findChildOfType(myFixture.file, PsiComment::class.java)
                ?: error("comment not found")
        assertIsNotEmptyTokenizer(strategy.getTokenizer(comment))
    }

    fun testNumberIsNotTokenized() {
        myFixture.configureByText("test.ron", "42")
        val numberLeaf = myFixture.file.findElementAt(0) ?: error("number not found")
        assertEquals(SpellcheckingStrategy.EMPTY_TOKENIZER, strategy.getTokenizer(numberLeaf))
    }

    fun testKeywordIsNotTokenized() {
        myFixture.configureByText("test.ron", "true")
        val keyword = myFixture.file.findElementAt(0) ?: error("keyword not found")
        assertEquals(SpellcheckingStrategy.EMPTY_TOKENIZER, strategy.getTokenizer(keyword))
    }

    fun testRawStringIsTokenized() {
        myFixture.configureByText("test.ron", """{"key": r#"hello world"#}""")
        val string =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStringVal::class.java)
                ?: error("RonStringVal not found")
        val tokenizer = strategy.getTokenizer(string)
        assertIsNotEmptyTokenizer(tokenizer)
        assertEquals(RonStringTokenizer, tokenizer)
    }

    fun testStandardStringIsTokenized() {
        myFixture.configureByText("test.ron", """{"key": "hello world"}""")
        val string =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStringVal::class.java)
                ?: error("RonStringVal not found")
        assertEquals(RonStringTokenizer, strategy.getTokenizer(string))
    }

    fun testCharLiteralIsNotTokenized() {
        myFixture.configureByText("test.ron", "'a'")
        val charVal =
            PsiTreeUtil.findChildOfType(
                myFixture.file,
                RonCharVal::class.java,
            ) ?: error("RonCharVal not found")
        assertEquals(SpellcheckingStrategy.EMPTY_TOKENIZER, strategy.getTokenizer(charVal))
    }

    private fun assertIsNotEmptyTokenizer(tokenizer: Tokenizer<*>) {
        assertNotSame(
            "Expected a non-empty tokenizer",
            SpellcheckingStrategy.EMPTY_TOKENIZER,
            tokenizer,
        )
    }
}
