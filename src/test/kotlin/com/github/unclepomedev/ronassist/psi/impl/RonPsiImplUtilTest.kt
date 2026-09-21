package com.github.unclepomedev.ronassist.psi.impl

import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonPsiImplUtilTest : BasePlatformTestCase() {

    fun testStringContentRangeStandardAndRawStrings() {
        assertEquals(RonPsiImplUtil.stringContentRange("\"hello\""), TextRange(1, 6))
        assertEquals(RonPsiImplUtil.stringContentRange("r\"hello\""), TextRange(2, 7))
        assertEquals(RonPsiImplUtil.stringContentRange("r#\"hello\"#"), TextRange(3, 8))
        assertEquals(RonPsiImplUtil.stringContentRange("r##\"hello\"##"), TextRange(4, 9))
    }

    fun testStringContentRangeByteStrings() {
        assertEquals(RonPsiImplUtil.stringContentRange("b\"hello\""), TextRange(2, 7))
        assertEquals(RonPsiImplUtil.stringContentRange("br\"hello\""), TextRange(3, 8))
        assertEquals(RonPsiImplUtil.stringContentRange("br#\"hello\"#"), TextRange(4, 9))
        assertEquals(RonPsiImplUtil.stringContentRange("br##\"hello\"##"), TextRange(5, 10))
    }

    fun testStringContentRangePrefixEdgeCases() {
        assertNull(RonPsiImplUtil.stringContentRange("b"))
        assertNull(RonPsiImplUtil.stringContentRange("bar"))
        assertNull(RonPsiImplUtil.stringContentRange("b\"unclosed"))
        assertNull(RonPsiImplUtil.stringContentRange("br#\"unclosed"))
    }

    fun testNormalizedIdentifier() {
        myFixture.configureByText("test.ron", "(r#type: 1, normal: 2)")
        val entries =
            PsiTreeUtil.findChildrenOfType(myFixture.file, RonStructEntry::class.java).toList()
        val rawIdent = RonPsiImplUtil.getNameIdentifier(entries[0])!!
        val normalIdent = RonPsiImplUtil.getNameIdentifier(entries[1])!!

        assertEquals("type", RonPsiImplUtil.normalizedIdentifier(rawIdent))
        assertEquals("normal", RonPsiImplUtil.normalizedIdentifier(normalIdent))
    }
}
