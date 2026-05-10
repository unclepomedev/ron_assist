package com.github.unclepomedev.ronassist.lang

import com.intellij.lang.Language

class RonLanguage private constructor() : Language("RON") {
    companion object {
        val INSTANCE = RonLanguage()
    }
}
