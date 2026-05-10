package com.github.unclepomedev.ronassist.parser

import com.github.unclepomedev.ronassist.lang.RonFileType
import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class RonFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RonLanguage.INSTANCE) {
    override fun getFileType(): FileType = RonFileType.INSTANCE
    override fun toString(): String = "RON File"
}
