package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.parser.RonFile
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonPsiPresentationTest : BasePlatformTestCase() {

    fun testPreviewPrimitiveString() =
        assertPreview(
            """{"key": "value"}""",
            primaryExpected = "\"key\"",
            previewExpected = "= \"value\"",
        )

    fun testPreviewPrimitiveNumber() =
        assertPreview(
            """{"count": 42}""",
            primaryExpected = "\"count\"",
            previewExpected = "= 42",
        )

    fun testPreviewPrimitiveBool() =
        assertPreview(
            """{"enabled": true}""",
            primaryExpected = "\"enabled\"",
            previewExpected = "= true",
        )

    fun testPreviewNestedMap() =
        assertPreview(
            """{"config": {"a": 1, "b": 2}}""",
            primaryExpected = "\"config\"",
            previewExpected = "= {2}",
        )

    fun testPreviewNestedList() =
        assertPreview(
            """{"items": [1, 2, 3]}""",
            primaryExpected = "\"items\"",
            previewExpected = "= [3]",
        )

    fun testPreviewNamedStruct() =
        assertPreview(
            """{"player": Player(name: "Reimu")}""",
            primaryExpected = "\"player\"",
            previewExpected = "= Player(...)",
        )

    fun testPreviewAnonymousTuple() =
        assertPreview(
            """{"coords": (10, 20)}""",
            primaryExpected = "\"coords\"",
            previewExpected = "= (...)",
        )

    fun testStructEntryPreview() =
        assertStructEntryPreview(
            """Config(width: 800, height: 600)""",
            entryName = "width",
            primaryExpected = "width",
            previewExpected = "= 800",
        )

    fun testStructNameLabel() =
        assertRootLabel(
            """Player(name: "Reimu")""",
            expected = "Player(...)",
        )

    fun testAnonymousTupleLabel() =
        assertRootLabel(
            """("a", "b", "c")""",
            expected = "(...)",
        )

    fun testMapLabelWithCount() =
        assertRootLabel(
            """{"a": 1, "b": 2, "c": 3}""",
            expected = "{3}",
        )

    fun testListLabelWithCount() =
        assertRootLabel(
            """[10, 20, 30, 40]""",
            expected = "[4]",
        )

    fun testPreviewRawStringWithNewlines() =
        assertPreview(
            """{"key": r#"line1
line2
line3"#}""",
            primaryExpected = "\"key\"",
            previewExpected = "= r#\"line1 line2 line3\"#",
        )

    fun testPrimaryLabelWithNewlines() =
        assertRootLabel(
            """r"multi
line" """,
            expected = "r\"multi line\"",
        )

    fun testTooltipMapEntry() {
        myFixture.configureByText("test.ron", """{"key": "value"}""")
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java) ?: error("entry not found")
        assertEquals("\"key\" = \"value\"", RonPsiPresentation.tooltipFor(entry))
    }

    fun testTooltipNamedStructWithFields() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu", hp: 100)""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertEquals("Player with 2 fields", RonPsiPresentation.tooltipFor(struct))
    }

    fun testTooltipSingleFieldStruct() {
        myFixture.configureByText("test.ron", """Single(only: 1)""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertEquals("Single with 1 field", RonPsiPresentation.tooltipFor(struct))
    }

    fun testTooltipAnonymousTupleReturnsNull() {
        myFixture.configureByText("test.ron", """(1, 2, 3)""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertNull(RonPsiPresentation.tooltipFor(struct))
    }

    fun testTooltipEmptyNamedStruct() {
        myFixture.configureByText("test.ron", """Empty()""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertEquals("Empty (empty)", RonPsiPresentation.tooltipFor(struct))
    }

    fun testTooltipNamedTuple() {
        myFixture.configureByText("test.ron", """Point(10, 20)""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertEquals("Point with 2 values", RonPsiPresentation.tooltipFor(struct))
    }

    fun testTooltipNamedTupleSingleValue() {
        myFixture.configureByText("test.ron", """Point(10)""")
        val file = myFixture.file as RonFile
        val struct =
            PsiTreeUtil.findChildOfType(file, RonStructOrTuple::class.java)
                ?: error("struct not found")
        assertEquals("Point with 1 value", RonPsiPresentation.tooltipFor(struct))
    }

    fun testPreviewChar() =
        assertPreview(
            """{"char": 'a'}""",
            primaryExpected = "\"char\"",
            previewExpected = "= 'a'",
        )

    fun testTooltipStructEntry() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonStructEntry::class.java)
                ?: error("entry not found")
        assertEquals("name = \"Reimu\"", RonPsiPresentation.tooltipFor(entry))
    }

    fun testSortKeyMapEntries() {
        myFixture.configureByText(
            "test.ron",
            """{
                "Regular": 1,
                r#"Raw"#: 2,
                b"Byte": 3,
                br#"RawByte"#: 4,
                r#RawIdent: 5,
                NormalIdent: 6,
                42: 7,
                true: 8,
                "plain": 9,
            }""",
        )
        val file = myFixture.file as RonFile
        val entries = PsiTreeUtil.findChildrenOfType(file, RonMapEntry::class.java).toList()
        assertEquals(9, entries.size)

        assertEquals("regular", RonPsiPresentation.sortKey(entries[0]))
        assertEquals("raw", RonPsiPresentation.sortKey(entries[1]))
        assertEquals("byte", RonPsiPresentation.sortKey(entries[2]))
        assertEquals("rawbyte", RonPsiPresentation.sortKey(entries[3]))
        assertEquals("rawident", RonPsiPresentation.sortKey(entries[4]))
        assertEquals("normalident", RonPsiPresentation.sortKey(entries[5]))
        assertEquals("42", RonPsiPresentation.sortKey(entries[6]))
        assertEquals("true", RonPsiPresentation.sortKey(entries[7]))
        assertEquals("plain", RonPsiPresentation.sortKey(entries[8]))
    }

    fun testSortKeyStructAsMapKey() {
        myFixture.configureByText(
            "test.ron",
            "{Foo(x: 1): 1}",
        )
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java) ?: error("entry not found")
        assertEquals("foo(...)", RonPsiPresentation.sortKey(entry))
    }

    fun testSortKeyMultilineStructAsMapKey() {
        myFixture.configureByText(
            "test.ron",
            """{
                Foo(
                    x: 1,
                    y: 2,
                ): 1
            }""",
        )
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java) ?: error("entry not found")
        val sortKey = RonPsiPresentation.sortKey(entry)
        assertEquals("foo(...)", sortKey)
        assertFalse(sortKey.contains('\n'))
    }

    fun testSortKeyIncompleteStringMapEntry() {
        myFixture.configureByText(
            "test.ron",
            "{\n\"unclosed\n: 1\n}",
        )
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java) ?: error("entry not found")
        // stringContentRange returns null for unclosed string like "unclosed,
        // so raw text is kept as-is and lowercased
        assertEquals("\"unclosed", RonPsiPresentation.sortKey(entry))
    }

    fun testSortKeyStructEntriesAndNames() {
        myFixture.configureByText(
            "test.ron",
            """(
                r#MyStruct(r#my_field: 1, normal_field: 2),
                (anon_field: 3),
            )""",
        )
        val file = myFixture.file as RonFile
        val structs = PsiTreeUtil.findChildrenOfType(file, RonStructOrTuple::class.java).toList()
        // structs[0] is root anonymous tuple, structs[1] is r#MyStruct, structs[2] is anonymous
        // tuple (anon_field: 3)
        val namedStruct = structs[1]
        val anonTuple = structs[2]

        assertEquals("mystruct(...)", RonPsiPresentation.sortKey(namedStruct))
        assertEquals("(...)", RonPsiPresentation.sortKey(anonTuple))

        val entries = PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java).toList()
        assertEquals("my_field", RonPsiPresentation.sortKey(entries[0]))
        assertEquals("normal_field", RonPsiPresentation.sortKey(entries[1]))
        assertEquals("anon_field", RonPsiPresentation.sortKey(entries[2]))
    }

    private fun assertPreview(
        content: String,
        primaryExpected: String,
        previewExpected: String,
    ) {
        myFixture.configureByText("test.ron", content)
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java)
                ?: error("RonMapEntry not found in: $content")
        assertEquals(primaryExpected, RonPsiPresentation.primaryLabel(entry))
        assertEquals(previewExpected, RonPsiPresentation.locationPreview(entry))
    }

    @Suppress("SameParameterValue")
    private fun assertStructEntryPreview(
        content: String,
        entryName: String,
        primaryExpected: String,
        previewExpected: String,
    ) {
        myFixture.configureByText("test.ron", content)
        val file = myFixture.file as RonFile
        val entry =
            PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java).firstOrNull {
                RonPsiPresentation.primaryLabel(it) == entryName
            } ?: error("RonStructEntry with name '$entryName' not found in: $content")
        assertEquals(primaryExpected, RonPsiPresentation.primaryLabel(entry))
        assertEquals(previewExpected, RonPsiPresentation.locationPreview(entry))
    }

    private fun assertRootLabel(content: String, expected: String) {
        myFixture.configureByText("test.ron", content)
        val file = myFixture.file as RonFile
        val root =
            PsiTreeUtil.findChildOfType(
                    file,
                    com.github.unclepomedev.ronassist.psi.RonValue::class.java,
                )
                ?.firstChild ?: error("Root container not found")
        assertEquals(expected, RonPsiPresentation.primaryLabel(root))
    }
}
