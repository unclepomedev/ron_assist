package com.github.unclepomedev.ronassist.psi.impl

import com.intellij.openapi.util.TextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RonPsiImplUtilTest {

    @Test
    fun testStringContentRangeStandardAndRawStrings() {
        assertEquals(RonPsiImplUtil.stringContentRange("\"hello\""), TextRange(1, 6))
        assertEquals(RonPsiImplUtil.stringContentRange("r\"hello\""), TextRange(2, 7))
        assertEquals(RonPsiImplUtil.stringContentRange("r#\"hello\"#"), TextRange(3, 8))
        assertEquals(RonPsiImplUtil.stringContentRange("r##\"hello\"##"), TextRange(4, 9))
    }

    @Test
    fun testStringContentRangeByteStrings() {
        assertEquals(RonPsiImplUtil.stringContentRange("b\"hello\""), TextRange(2, 7))
        assertEquals(RonPsiImplUtil.stringContentRange("br\"hello\""), TextRange(3, 8))
        assertEquals(RonPsiImplUtil.stringContentRange("br#\"hello\"#"), TextRange(4, 9))
        assertEquals(RonPsiImplUtil.stringContentRange("br##\"hello\"##"), TextRange(5, 10))
    }

    @Test
    fun testStringContentRangePrefixEdgeCases() {
        assertNull(RonPsiImplUtil.stringContentRange("b"))
        assertNull(RonPsiImplUtil.stringContentRange("bar"))
        assertNull(RonPsiImplUtil.stringContentRange("b\"unclosed"))
        assertNull(RonPsiImplUtil.stringContentRange("br#\"unclosed"))
    }
}
