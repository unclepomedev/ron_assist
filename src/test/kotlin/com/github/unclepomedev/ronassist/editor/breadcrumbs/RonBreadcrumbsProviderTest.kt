package com.github.unclepomedev.ronassist.editor.breadcrumbs

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonBreadcrumbsProviderTest : BasePlatformTestCase() {

    private val provider = RonBreadcrumbsProvider()

    fun testAcceptsMapEntry() {
        myFixture.configureByText("test.ron", """{"key": "value"}""")
        val entry =
            PsiTreeUtil.findChildOfType(myFixture.file, RonMapEntry::class.java)
                ?: error("RonMapEntry not found")
        assertTrue(provider.acceptElement(entry))
    }

    fun testAcceptsStructEntry() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val entry =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructEntry::class.java)
                ?: error("RonStructEntry not found")
        assertTrue(provider.acceptElement(entry))
    }

    fun testAcceptsNamedStruct() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val struct =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
                ?: error("RonStructOrTuple not found")
        assertTrue(provider.acceptElement(struct))
    }

    fun testRejectsAnonymousTuple() {
        myFixture.configureByText("test.ron", """(1, 2, 3)""")
        val struct =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
                ?: error("RonStructOrTuple not found")
        assertFalse(provider.acceptElement(struct))
    }

    fun testLanguagesContainsRonLanguage() {
        val languages = provider.languages
        assertEquals(1, languages.size)
        assertEquals(RonLanguage.INSTANCE, languages[0])
    }

    fun testGetElementInfoAndTooltip() {
        myFixture.configureByText("test.ron", """Player(name: "Reimu")""")
        val struct =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructOrTuple::class.java)
                ?: error("RonStructOrTuple not found")
        assertEquals("Player(...)", provider.getElementInfo(struct))
        assertEquals("Player with 1 field", provider.getElementTooltip(struct))

        val entry =
            PsiTreeUtil.findChildOfType(myFixture.file, RonStructEntry::class.java)
                ?: error("RonStructEntry not found")
        assertEquals("name", provider.getElementInfo(entry))
        assertEquals("name = \"Reimu\"", provider.getElementTooltip(entry))
    }
}
