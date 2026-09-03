package com.github.unclepomedev.ronassist.formatting

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

class RonCodeStyleSettings(settings: CodeStyleSettings) :
    CustomCodeStyleSettings("RonCodeStyleSettings", settings) {

    /**
     * When enabled, the formatter appends a trailing comma after the last element of multiline
     * lists, maps, structs and tuples. Disabled by default (opt-in).
     */
    @JvmField var addTrailingComma: Boolean = false
}
