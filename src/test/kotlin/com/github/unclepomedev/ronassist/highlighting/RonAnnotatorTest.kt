package com.github.unclepomedev.ronassist.highlighting

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonAnnotatorTest : BasePlatformTestCase() {

    fun testStructNameHighlighted() {
        myFixture.configureByText("test.ron", "Player(name: \"Reimu\")")
        val infos = highlightInfosWithKey(RonSyntaxHighlighter.STRUCT_NAME)
        assertEquals(1, infos.size)
        assertEquals("Player", textOf(infos[0]))
    }

    fun testFieldNameHighlighted() {
        myFixture.configureByText("test.ron", "Player(name: \"Reimu\", hp: 100)")
        val infos = highlightInfosWithKey(RonSyntaxHighlighter.FIELD_NAME)
        assertEquals(2, infos.size)
        assertEquals(setOf("name", "hp"), infos.map { textOf(it) }.toSet())
    }

    fun testNestedStructNamesHighlighted() {
        myFixture.configureByText("test.ron", "Outer(inner: Inner(x: 1))")
        val infos = highlightInfosWithKey(RonSyntaxHighlighter.STRUCT_NAME)
        assertEquals(2, infos.size)
        assertEquals(setOf("Outer", "Inner"), infos.map { textOf(it) }.toSet())
    }

    fun testAnonymousTupleNotHighlightedAsStruct() {
        myFixture.configureByText("test.ron", "(1, 2, 3)")
        val infos = highlightInfosWithKey(RonSyntaxHighlighter.STRUCT_NAME)
        assertEquals(0, infos.size)
    }

    fun testEnumVariantPositionNotHighlighted() {
        myFixture.configureByText("test.ron", "(difficulty: Easy)")
        val structInfos = highlightInfosWithKey(RonSyntaxHighlighter.STRUCT_NAME)
        val fieldInfos = highlightInfosWithKey(RonSyntaxHighlighter.FIELD_NAME)
        assertEquals(0, structInfos.size)
        assertEquals(1, fieldInfos.size)
        assertEquals(setOf("difficulty"), fieldInfos.map { textOf(it) }.toSet())
    }

    private fun highlightInfosWithKey(key: TextAttributesKey): List<HighlightInfo> {
        return myFixture.doHighlighting().filter { it.forcedTextAttributesKey == key }
    }

    private fun textOf(info: HighlightInfo): String =
        myFixture.editor.document.getText(
            com.intellij.openapi.util.TextRange(info.startOffset, info.endOffset)
        )
}
