package com.github.unclepomedev.ronassist.parser

import com.github.unclepomedev.ronassist.psi.RonAttribute
import com.github.unclepomedev.ronassist.psi.RonValue
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil

class RonAttributeParserTest : RonParsingTestCaseBase() {
    fun testTypeAttribute() {
        val file =
            createPsiFile("attribute.ron", "// config\n#![type = \"example::Config\"]\n(host: 1)")
        assertNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
        val attribute = file.children.filterIsInstance<RonAttribute>().single()
        assertEquals("type", attribute.identifier?.text)
        assertEquals("\"example::Config\"", attribute.stringVal?.text)
        assertEquals(
            "host",
            file.children
                .filterIsInstance<RonValue>()
                .single()
                .structOrTuple!!
                .structEntryList
                .single()
                .identifier
                .text,
        )
    }

    fun testMissingBracketPreservesBody() {
        val file = createPsiFile("attribute.ron", "#![type = \"example::Config\"\n(host: 1)")
        assertNotNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
        assertNotNull(file.children.filterIsInstance<RonValue>().single().structOrTuple)
    }

    fun testMissingValuePreservesBody() {
        val file = createPsiFile("attribute.ron", "#![type =\n(host: 1)")
        assertNotNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
        assertNotNull(file.children.filterIsInstance<RonValue>().single().structOrTuple)
    }

    fun testCommentsAndStringsAreNotAttributes() {
        val file =
            createPsiFile(
                "attribute.ron",
                "// #![type = \"example::Config\"]\n\"#![type = \\\"example::Config\\\"]\"",
            )
        assertTrue(file.children.filterIsInstance<RonAttribute>().isEmpty())
        assertNull(PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java))
    }
}
