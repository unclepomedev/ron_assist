package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil

class RonSmartEnterProcessor : SmartEnterProcessor() {

    /**
     * Completes the current entry by ensuring it ends with a comma, then
     * inserts a new line positioned for the next entry.
     */
    override fun process(project: Project, editor: Editor, file: PsiFile): Boolean {
        commit(editor)

        val entry = findEnclosingEntry(file, editor.caretModel.offset) ?: return false
        if (containsError(entry)) return false

        insertNewLineAfterEntry(project, editor, entry)
        indentAtCaret(project, editor, file)
        return true
    }

    private fun insertNewLineAfterEntry(project: Project, editor: Editor, entry: PsiElement) {
        val existingComma = trailingCommaOf(entry)
        val offset = existingComma?.textRange?.endOffset ?: endOffsetSkippingTrailingWhitespace(entry)
        val text = if (existingComma != null) "\n" else ",\n"

        editor.document.insertString(offset, text)
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        editor.caretModel.moveToOffset(offset + text.length)
    }

    private fun indentAtCaret(project: Project, editor: Editor, file: PsiFile) {
        val adjusted = CodeStyleManager.getInstance(project)
            .adjustLineIndent(file, editor.caretModel.offset)
        editor.caretModel.moveToOffset(adjusted)
    }

    /**
     * Looks for an entry by checking both sides of the caret. When the caret
     * sits right after a comma, we resolve to the entry preceding that comma
     * rather than to a containing outer entry.
     */
    private fun findEnclosingEntry(file: PsiFile, offset: Int): PsiElement? {
        val before = file.findElementAt(maxOf(0, offset - 1))
        entryPrecedingComma(before)?.let { return it }
        before?.let { entryAncestorOf(it) }?.let { return it }
        return file.findElementAt(offset)?.let { entryAncestorOf(it) }
    }

    private fun entryAncestorOf(element: PsiElement): PsiElement? =
        PsiTreeUtil.getParentOfType(
            element,
            RonStructEntry::class.java,
            RonMapEntry::class.java,
        )

    private fun entryPrecedingComma(element: PsiElement?): PsiElement? {
        if (element?.node?.elementType != RonTypes.COMMA) return null
        var prev: PsiElement? = element.prevSibling
        while (prev is PsiWhiteSpace) prev = prev.prevSibling
        return prev?.takeIf { it is RonStructEntry || it is RonMapEntry }
    }

    private fun trailingCommaOf(entry: PsiElement): PsiElement? {
        var next = entry.nextSibling
        while (next is PsiWhiteSpace) next = next.nextSibling
        return next?.takeIf { it.node?.elementType == RonTypes.COMMA }
    }

    private fun endOffsetSkippingTrailingWhitespace(entry: PsiElement): Int {
        var last = entry.lastChild
        while (last is PsiWhiteSpace) last = last.prevSibling ?: break
        return last?.textRange?.endOffset ?: entry.textRange.endOffset
    }

    private fun containsError(entry: PsiElement): Boolean =
        PsiTreeUtil.findChildOfType(entry, PsiErrorElement::class.java) != null
}
