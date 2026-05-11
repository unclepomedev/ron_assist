package com.github.unclepomedev.ronassist.editor.breadcrumbs

import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonBreadcrumbsProviderTest : BasePlatformTestCase() {

    private val provider = RonBreadcrumbsProvider()

    fun testAcceptCompleteMapEntry() {
        val entry = parseMapEntry("""{"key": "value"}""")
        assertTrue(provider.acceptElement(entry))
    }

    fun testRejectMapEntryWithoutKey() {
        val entry = findMapEntryOrNull(""": "value"""") ?: return
        assertFalse(provider.acceptElement(entry))
    }

    fun testAcceptCompleteStructEntry() {
        val entry = parseStructEntry("""Player(name: "Reimu")""")
        assertTrue(provider.acceptElement(entry))
    }

    fun testRejectStructEntryWithoutName() {
        val entry = findStructEntryOrNull("""Player(: "Reimu")""") ?: return
        assertFalse(provider.acceptElement(entry))
    }

    fun testAcceptNamedStructOrTuple() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val struct = PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
            ?: error("RonStructOrTuple not found")
        assertTrue(provider.acceptElement(struct))
    }

    fun testRejectAnonymousStructOrTuple() {
        myFixture.configureByText("test.ron", """(1, 2, 3)""")
        val struct = PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
            ?: error("RonStructOrTuple not found")
        assertFalse(provider.acceptElement(struct))
    }

    // suppress_warning is used to isolate concerns and make it easier to understand the differences for each.
    @Suppress("SameParameterValue")
    private fun parseMapEntry(content: String): RonMapEntry {
        myFixture.configureByText("test.ron", content)
        return PsiTreeUtil.findChildOfType(myFixture.file, RonMapEntry::class.java)
            ?: error("RonMapEntry not found in: $content")
    }

    @Suppress("SameParameterValue")
    private fun findMapEntryOrNull(content: String): RonMapEntry? {
        myFixture.configureByText("test.ron", content)
        return PsiTreeUtil.findChildOfType(myFixture.file, RonMapEntry::class.java)
    }

    @Suppress("SameParameterValue")
    private fun parseStructEntry(content: String): RonStructEntry {
        myFixture.configureByText("test.ron", content)
        return PsiTreeUtil.findChildOfType(myFixture.file, RonStructEntry::class.java)
            ?: error("RonStructEntry not found in: $content")
    }

    @Suppress("SameParameterValue")
    private fun findStructEntryOrNull(content: String): RonStructEntry? {
        myFixture.configureByText("test.ron", content)
        return PsiTreeUtil.findChildOfType(myFixture.file, RonStructEntry::class.java)
    }
}
