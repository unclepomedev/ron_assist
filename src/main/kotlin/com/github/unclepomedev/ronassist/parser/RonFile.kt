package com.github.unclepomedev.ronassist.parser

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class RonFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RonLanguage.INSTANCE) {
    override fun getFileType(): FileType = RON_FILE_TYPE
    override fun toString(): String = "RON File"
}
