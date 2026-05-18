package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor

class RonPostFormatProcessor : PostFormatProcessor {

    /**
     * Post-processes the PSI element after formatting, ensuring that RON files
     * end with a single POSIX-compliant trailing newline.
     */
    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        if (source is PsiFile
            && source.fileType == RON_FILE_TYPE
            && !InjectedLanguageManager.getInstance(source.project).isInjectedFragment(source)
        ) {
            ensurePosixNewline(source)
        }
        return source
    }

    /**
     * Enforces a trailing newline if the formatted range reaches the end of the file,
     * adjusting and returning the updated text range based on the modification length.
     */
    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.fileType == RON_FILE_TYPE
            && !InjectedLanguageManager.getInstance(source.project).isInjectedFragment(source)
            && rangeToReformat.endOffset >= source.textLength
        ) {
            val delta = ensurePosixNewline(source)
            if (delta != 0) {
                val newEndOffset = maxOf(rangeToReformat.startOffset, rangeToReformat.endOffset + delta)
                return TextRange(rangeToReformat.startOffset, newEndOffset)
            }
        }
        return rangeToReformat
    }

    /**
     * Cleans up trailing whitespaces to guarantee a single newline character at EOF,
     * returning the net change in document length (delta) to adjust formatting ranges.
     */
    private fun ensurePosixNewline(file: PsiFile): Int {
        val documentManager = PsiDocumentManager.getInstance(file.project)
        val document = documentManager.getDocument(file) ?: return 0

        val chars = document.charsSequence
        val oldLength = chars.length

        val lastNonWsIndex = chars.indexOfLast { !it.isWhitespace() }

        val replaceStart = lastNonWsIndex + 1

        val trailingLength = oldLength - replaceStart
        if (trailingLength == 1 && chars[replaceStart] == '\n') {
            return 0
        }

        document.replaceString(replaceStart, oldLength, "\n")
        documentManager.commitDocument(document)

        return document.textLength - oldLength
    }
}
