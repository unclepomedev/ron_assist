package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.application.options.CodeStyle
import com.intellij.application.options.CodeStyleSchemesConfigurable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RonCodeStyleSettingsUITest : BasePlatformTestCase() {

    fun testProviderHasNoStandaloneSettingsPage() {
        assertFalse(
            "RonCodeStyleSettingsProvider must not expose its own settings page",
            RonCodeStyleSettingsProvider().hasSettingsPage(),
        )
    }

    fun testLanguageProviderIsRegisteredForRon() {
        val provider = LanguageCodeStyleSettingsProvider.forLanguage(RonLanguage.INSTANCE)
            ?: error("LanguageCodeStyleSettingsProvider for RON not registered")
        assertTrue(provider is RonLanguageCodeStyleSettingsProvider)
        assertFalse(
            provider.getCodeSample(LanguageCodeStyleSettingsProvider.SettingsType.INDENT_SETTINGS).isNullOrBlank()
        )
    }

    fun testCodeStyleConfigurableUISmoke() {
        val schemesConfigurable = CodeStyleSchemesConfigurable(project)
        try {
            val ronConfigurable = schemesConfigurable.configurables
                .firstOrNull { it.displayName == RonLanguage.INSTANCE.displayName }
                ?: error("RON Code Style configurable not found")
            assertNotNull("RON page must create a UI component", ronConfigurable.createComponent())
            ronConfigurable.reset()
            assertFalse(ronConfigurable.isModified)
        } finally {
            schemesConfigurable.disposeUIResources()
        }
    }

    fun testCustomSettingsAreAvailable() {
        val settings = CodeStyle.createTestSettings()
        val custom = settings.getCustomSettings(RonCodeStyleSettings::class.java)
        assertNotNull(custom)
        assertFalse("addTrailingComma must be opt-in (disabled by default)", custom.addTrailingComma)
    }
}
