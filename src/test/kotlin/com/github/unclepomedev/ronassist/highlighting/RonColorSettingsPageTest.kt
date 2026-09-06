package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.icons.RonIcons
import com.intellij.openapi.options.colors.ColorDescriptor
import org.junit.Assert.*
import org.junit.Test

class RonColorSettingsPageTest {

    private val page = RonColorSettingsPage()

    @Test
    fun testDisplayNameAndIcon() {
        assertEquals("RON", page.displayName)
        assertEquals(RonIcons.FILE, page.icon)
    }

    @Test
    fun testHighlighter() {
        val highlighter = page.highlighter
        assertTrue(highlighter is RonSyntaxHighlighter)
    }

    @Test
    fun testDescriptors() {
        val descriptors = page.attributeDescriptors
        assertTrue(descriptors.isNotEmpty())
        assertArrayEquals(ColorDescriptor.EMPTY_ARRAY, page.colorDescriptors)

        val keys = descriptors.map { it.key }.toSet()
        assertTrue(keys.contains(RonSyntaxHighlighter.KEYWORD))
        assertTrue(keys.contains(RonSyntaxHighlighter.NUMBER))
        assertTrue(keys.contains(RonSyntaxHighlighter.STRING))
        assertTrue(keys.contains(RonSyntaxHighlighter.RAW_STRING))
        assertTrue(keys.contains(RonSyntaxHighlighter.CHAR))
        assertTrue(keys.contains(RonSyntaxHighlighter.LINE_COMMENT))
        assertTrue(keys.contains(RonSyntaxHighlighter.BLOCK_COMMENT))
        assertTrue(keys.contains(RonSyntaxHighlighter.IDENTIFIER))
        assertTrue(keys.contains(RonSyntaxHighlighter.STRUCT_NAME))
        assertTrue(keys.contains(RonSyntaxHighlighter.FIELD_NAME))
        assertTrue(keys.contains(RonSyntaxHighlighter.BRACES))
        assertTrue(keys.contains(RonSyntaxHighlighter.BRACKETS))
        assertTrue(keys.contains(RonSyntaxHighlighter.PARENTHESES))
        assertTrue(keys.contains(RonSyntaxHighlighter.COMMA))
        assertTrue(keys.contains(RonSyntaxHighlighter.BAD_CHARACTER))
    }

    @Test
    fun testAdditionalHighlightingTags() {
        val map = page.additionalHighlightingTagToDescriptorMap
        assertNotNull(map)
        assertEquals(RonSyntaxHighlighter.STRUCT_NAME, map["structName"])
        assertEquals(RonSyntaxHighlighter.FIELD_NAME, map["fieldName"])
    }

    @Test
    fun testDemoTextContainsHighlightingTags() {
        val demoText = page.demoText
        assertTrue(demoText.isNotEmpty())
        assertTrue(demoText.contains("<structName>"))
        assertTrue(demoText.contains("</structName>"))
        assertTrue(demoText.contains("<fieldName>"))
        assertTrue(demoText.contains("</fieldName>"))
    }
}
