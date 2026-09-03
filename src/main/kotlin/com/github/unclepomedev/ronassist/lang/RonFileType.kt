package com.github.unclepomedev.ronassist.lang

import com.github.unclepomedev.ronassist.icons.RonIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

val RON_FILE_TYPE = RonFileType()

class RonFileType : LanguageFileType(RonLanguage.INSTANCE) {
    override fun getName(): String = "RON"

    override fun getDescription(): String = "Rust object notation"

    override fun getDefaultExtension(): String = "ron"

    override fun getIcon(): Icon = RonIcons.FILE
}
