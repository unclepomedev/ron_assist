package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
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
            addTrailingCommas(source, settings)
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
        ) {
            val reachesEof = rangeToReformat.endOffset >= source.textLength
            val commaDelta = addTrailingCommas(source, settings, rangeToReformat)
            val newlineDelta = if (reachesEof) {
                ensurePosixNewline(source)
            } else {
                0
            }
            val delta = commaDelta + newlineDelta
            if (delta != 0) {
                val newEndOffset = maxOf(rangeToReformat.startOffset, rangeToReformat.endOffset + delta)
                return TextRange(rangeToReformat.startOffset, newEndOffset)
            }
        }
        return rangeToReformat
    }

    /**
     * Appends a trailing comma after the last element of every multiline list, map,
     * struct or tuple when [RonCodeStyleSettings.addTrailingComma] is enabled,
     * optionally constrained to [range], returning the net change in document length.
     */
    private fun addTrailingCommas(file: PsiFile, settings: CodeStyleSettings, range: TextRange? = null): Int {
        if (!settings.getCustomSettings(RonCodeStyleSettings::class.java).addTrailingComma) return 0

        val documentManager = PsiDocumentManager.getInstance(file.project)
        val document = documentManager.getDocument(file) ?: return 0

        val closingTokens = setOf(RonTypes.RBRACK, RonTypes.RBRACE, RonTypes.RPAREN)
        val openingTokens = setOf(RonTypes.LBRACK, RonTypes.LBRACE, RonTypes.LPAREN)

        val insertionOffsets = PsiTreeUtil.collectElements(file) {
            it is RonList || it is RonMap || it is RonStructOrTuple
        }.mapNotNull { container ->
            val closer = container.lastChild?.takeIf { it.node.elementType in closingTokens } ?: return@mapNotNull null
            var prev = closer.prevSibling
            while (prev is PsiWhiteSpace || prev is PsiComment) prev = prev.prevSibling
            if (prev == null) return@mapNotNull null
            val prevType = prev.node.elementType
            if (prevType == RonTypes.COMMA || prevType in openingTokens) return@mapNotNull null
            // Only add a trailing comma when the closing bracket is on a different line.
            val between = document.charsSequence.subSequence(prev.textRange.endOffset, closer.textRange.startOffset)
            if (!between.contains('\n')) return@mapNotNull null
            val offset = prev.textRange.endOffset
            if (range != null && !range.contains(offset)) return@mapNotNull null
            offset
        }

        if (insertionOffsets.isEmpty()) return 0

        insertionOffsets.sortedDescending().forEach { offset ->
            document.insertString(offset, ",")
        }
        documentManager.commitDocument(document)

        return insertionOffsets.size
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
