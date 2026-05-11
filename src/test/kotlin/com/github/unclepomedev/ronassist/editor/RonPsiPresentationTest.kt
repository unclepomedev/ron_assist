package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.parser.RonFile
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonPsiPresentationTest : BasePlatformTestCase() {

    fun testPreviewPrimitiveString() = assertPreview(
        """{"key": "value"}""",
        primaryExpected = "\"key\"",
        previewExpected = "= \"value\"",
    )

    fun testPreviewPrimitiveNumber() = assertPreview(
        """{"count": 42}""",
        primaryExpected = "\"count\"",
        previewExpected = "= 42",
    )

    fun testPreviewPrimitiveBool() = assertPreview(
        """{"enabled": true}""",
        primaryExpected = "\"enabled\"",
        previewExpected = "= true",
    )

    fun testPreviewNestedMap() = assertPreview(
        """{"config": {"a": 1, "b": 2}}""",
        primaryExpected = "\"config\"",
        previewExpected = "= {2}",
    )

    fun testPreviewNestedList() = assertPreview(
        """{"items": [1, 2, 3]}""",
        primaryExpected = "\"items\"",
        previewExpected = "= [3]",
    )

    fun testPreviewNamedStruct() = assertPreview(
        """{"player": Player(name: "Reimu")}""",
        primaryExpected = "\"player\"",
        previewExpected = "= Player(...)",
    )

    fun testPreviewAnonymousTuple() = assertPreview(
        """{"coords": (10, 20)}""",
        primaryExpected = "\"coords\"",
        previewExpected = "= (...)",
    )

    fun testStructEntryPreview() = assertStructEntryPreview(
        """Config(width: 800, height: 600)""",
        entryName = "width",
        primaryExpected = "width",
        previewExpected = "= 800",
    )

    fun testStructNameLabel() = assertRootLabel(
        """Player(name: "Reimu")""",
        expected = "Player(...)",
    )

    fun testAnonymousTupleLabel() = assertRootLabel(
        """("a", "b", "c")""",
        expected = "(...)",
    )

    fun testMapLabelWithCount() = assertRootLabel(
        """{"a": 1, "b": 2, "c": 3}""",
        expected = "{3}",
    )

    fun testListLabelWithCount() = assertRootLabel(
        """[10, 20, 30, 40]""",
        expected = "[4]",
    )

    private fun assertPreview(
        content: String,
        primaryExpected: String,
        previewExpected: String,
    ) {
        myFixture.configureByText("test.ron", content)
        val file = myFixture.file as RonFile
        val entry = PsiTreeUtil.findChildOfType(file, RonMapEntry::class.java)
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
        val entry = PsiTreeUtil.findChildrenOfType(file, RonStructEntry::class.java)
            .first { RonPsiPresentation.primaryLabel(it) == entryName }
        assertEquals(primaryExpected, RonPsiPresentation.primaryLabel(entry))
        assertEquals(previewExpected, RonPsiPresentation.locationPreview(entry))
    }

    private fun assertRootLabel(content: String, expected: String) {
        myFixture.configureByText("test.ron", content)
        val file = myFixture.file as RonFile
        val root = PsiTreeUtil.findChildOfType(
            file,
            com.github.unclepomedev.ronassist.psi.RonValue::class.java,
        )?.firstChild ?: error("Root container not found")
        assertEquals(expected, RonPsiPresentation.primaryLabel(root))
    }
}
