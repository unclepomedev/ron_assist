package com.github.unclepomedev.ronassist.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class RonFileType private constructor() : LanguageFileType(RonLanguage.INSTANCE) {
    override fun getName(): String = "RON"
    override fun getDescription(): String = "Rust object notation"
    override fun getDefaultExtension(): String = "ron"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/ron.svg", RonFileType::class.java)

    companion object {
        val INSTANCE = RonFileType()
    }
}
