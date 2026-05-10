package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor

class RonPostFormatProcessor : PostFormatProcessor {

    /**
     * Post-processes the PSI element after formatting, ensuring the file ends with a
     * single POSIX-compliant trailing newline.
     */
    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        if (source is PsiFile && source.fileType == RON_FILE_TYPE) {
            ensurePosixNewline(source)
        }
        return source
    }

    /**
     * Post-processes the formatted text range, enforcing a trailing newline if the
     * formatted range reaches the end of the file.
     */
    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.fileType == RON_FILE_TYPE && rangeToReformat.endOffset >= source.textLength) {
            ensurePosixNewline(source)
        }
        return rangeToReformat
    }

    /**
     * Cleans up trailing whitespaces at the end of the document and guarantees
     * that it ends with exactly one newline character.
     */
    private fun ensurePosixNewline(file: PsiFile) {
        val project = file.project
        val documentManager = PsiDocumentManager.getInstance(project)
        val document = documentManager.getDocument(file) ?: return

        val chars = document.charsSequence
        var lastNonWsIndex = chars.length - 1

        while (lastNonWsIndex >= 0 && chars[lastNonWsIndex].isWhitespace()) {
            lastNonWsIndex--
        }
        if (lastNonWsIndex == -1) return

        val replaceStart = lastNonWsIndex + 1
        val trailingWhitespace = chars.subSequence(replaceStart, chars.length).toString()

        if (trailingWhitespace == "\n") return

        document.replaceString(replaceStart, chars.length, "\n")
        documentManager.commitDocument(document)
    }
}
