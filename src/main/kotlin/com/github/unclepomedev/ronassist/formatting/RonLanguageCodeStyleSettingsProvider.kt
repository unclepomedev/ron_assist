package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

class RonLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage() = RonLanguage.INSTANCE

    override fun getCodeSample(settingsType: SettingsType): String = """
        (
            key1: "value1",

            key2: "value2"
        )
    """.trimIndent()

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions
    ) {
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }
}
