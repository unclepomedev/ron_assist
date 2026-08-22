package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

class RonCodeStyleSettingsProvider : CodeStyleSettingsProvider() {

    override fun getLanguage() = RonLanguage.INSTANCE

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings {
        return RonCodeStyleSettings(settings)
    }
}
