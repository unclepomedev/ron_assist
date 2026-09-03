package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

class RonLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage() = RonLanguage.INSTANCE

    override fun createConfigurable(
        baseSettings: CodeStyleSettings,
        modelSettings: CodeStyleSettings,
    ): CodeStyleConfigurable {
        return object :
            CodeStyleAbstractConfigurable(
                baseSettings,
                modelSettings,
                RonLanguage.INSTANCE.displayName,
            ) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return RonCodeStyleMainPanel(currentSettings, settings)
            }
        }
    }

    private class RonCodeStyleMainPanel(
        currentSettings: CodeStyleSettings,
        settings: CodeStyleSettings,
    ) : TabbedLanguageCodeStylePanel(RonLanguage.INSTANCE, currentSettings, settings) {

        override fun initTabs(settings: CodeStyleSettings) {
            addIndentOptionsTab(settings)
            addWrappingAndBracesTab(settings)
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String =
        """
        (
            key1: "value1",

            key2: "value2"
        )
        """
            .trimIndent()

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions,
    ) {
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    override fun customizeSettings(
        consumer: CodeStyleSettingsCustomizable,
        settingsType: SettingsType,
    ) {
        if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showCustomOption(
                RonCodeStyleSettings::class.java,
                "addTrailingComma",
                "Add trailing comma",
                "Commas",
            )
        }
    }
}
