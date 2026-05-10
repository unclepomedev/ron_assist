package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor

class RonPostFormatProcessor : PostFormatProcessor {
    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        return source
    }

    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.fileType != RON_FILE_TYPE) return rangeToReformat

        val document = source.viewProvider.document ?: return rangeToReformat
        val text = document.text
        val eofOffset = document.textLength

        if (text.isNotEmpty() && !text.endsWith("\n") && rangeToReformat.endOffset >= eofOffset) {
            document.insertString(eofOffset, "\n")
            PsiDocumentManager.getInstance(source.project).commitDocument(document)
            return TextRange(rangeToReformat.startOffset, rangeToReformat.endOffset + 1)
        }

        return rangeToReformat
    }
}
