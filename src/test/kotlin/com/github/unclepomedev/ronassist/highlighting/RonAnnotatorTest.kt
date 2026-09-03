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

    // TODO: Move to gold files if multi-line test cases grow.

    fun testMissingCommaInList() {
        myFixture.configureByText(
            "test.ron",
            """
            [
                1
                <error descr="Missing comma">2</error>
                <error descr="Missing comma">3</error>
            ]
            """
                .trimIndent(),
        )
        myFixture.testHighlighting(false, false, false)
    }

    fun testMissingCommaInMap() {
        myFixture.configureByText(
            "test.ron",
            """
            {
                "a": 1
                <error descr="Missing comma">"b": 2</error>
            }
            """
                .trimIndent(),
        )
        myFixture.testHighlighting(false, false, false)
    }

    fun testMissingCommaInStruct() {
        myFixture.configureByText(
            "test.ron",
            """
            Player(
                hp: 100
                <error descr="Missing comma">mp: 50</error>
            )
            """
                .trimIndent(),
        )
        myFixture.testHighlighting(false, false, false)
    }

    fun testValidCommasNoErrors() {
        myFixture.configureByText("test_list.ron", "[1, 2, 3]")
        myFixture.testHighlighting(false, false, false)

        myFixture.configureByText("test_map.ron", "{ \"a\": 1, \"b\": 2 }")
        myFixture.testHighlighting(false, false, false)

        myFixture.configureByText("test_struct.ron", "Player(hp: 100, mp: 50)")
        myFixture.testHighlighting(false, false, false)
    }

    fun testTrailingCommaIsAllowed() {
        myFixture.configureByText(
            "test.ron",
            """
            [
                1,
                2,
            ]
            """
                .trimIndent(),
        )
        myFixture.testHighlighting(false, false, false)
    }

    fun testMissingCommaInTuple() {
        myFixture.configureByText(
            "test_tuple.ron",
            """
            (
                1
                <error descr="Missing comma">2</error>
            )
            """
                .trimIndent(),
        )
        myFixture.testHighlighting(false, false, false)
    }

    private fun highlightInfosWithKey(key: TextAttributesKey): List<HighlightInfo> {
        return myFixture.doHighlighting().filter { it.forcedTextAttributesKey == key }
    }

    private fun textOf(info: HighlightInfo): String =
        myFixture.editor.document.getText(
            com.intellij.openapi.util.TextRange(info.startOffset, info.endOffset)
        )
}
