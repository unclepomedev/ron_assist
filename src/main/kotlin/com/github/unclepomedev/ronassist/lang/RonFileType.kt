package com.github.unclepomedev.ronassist.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class RonFileType private constructor() : LanguageFileType(RonLanguage.INSTANCE) {
    override fun getName(): String = "RON"
    override fun getDescription(): String = "Rust object notation"
    override fun getDefaultExtension(): String = "ron"
    override fun getIcon(): Icon? = null

    companion object {
        val INSTANCE = RonFileType()
    }
}