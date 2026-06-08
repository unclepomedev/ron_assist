package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.github.unclepomedev.ronassist.psi.RonValue
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
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
        if (!isComplete(entry)) return false

        insertNewLineAfterEntry(project, editor, entry)
        indentAtCaret(project, editor, file)
        return true
    }

    private fun insertNewLineAfterEntry(project: Project, editor: Editor, entry: PsiElement) {
        val valueElement = when (entry) {
            is RonStructEntry -> RonPsiImplUtil.getValue(entry)
            is RonMapEntry -> RonPsiImplUtil.getValue(entry)
            is RonValue -> entry
            else -> null
        }

        val hasError = PsiTreeUtil.findChildOfType(entry, PsiErrorElement::class.java) != null
        val existingComma = if (hasError) null else trailingCommaOf(entry)

        val offset = existingComma?.textRange?.endOffset
            ?: valueElement?.textRange?.endOffset
            ?: endOffsetSkippingTrailingWhitespace(entry)

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
     * Locates the target element for Smart Enter completion based on the caret offset.
     *
     * If positioned immediately after a comma, this resolves to the preceding entry rather
     * than the containing parent. Otherwise, it evaluates candidate elements from the
     * innermost leaf upwards.
     *
     * To ensure completion occurs at the correct AST level, it prioritizes the innermost
     * complete element that lacks a trailing comma before its next sibling (e.g., resolving
     * to a list element rather than its nested struct field). Falls back to the immediate
     * innermost candidate if no prioritized element is found.
     */
    private fun findEnclosingEntry(file: PsiFile, offset: Int): PsiElement? {
        val before = file.findElementAt(maxOf(0, offset - 1))
        val start = entryPrecedingComma(before)
            ?: before?.let { candidateAncestorOf(it) }
            ?: file.findElementAt(offset)?.let { candidateAncestorOf(it) }
            ?: return null

        val chain = candidateChain(start)
        return chain.firstOrNull { isComplete(it) && missingCommaBeforeNext(it) }
            ?: chain.firstOrNull()
    }

    private fun isCandidate(element: PsiElement): Boolean =
        element is RonStructEntry ||
                element is RonMapEntry ||
                (element is RonValue && (element.parent is RonList || element.parent is RonStructOrTuple))

    private fun candidateAncestorOf(element: PsiElement): PsiElement? {
        var current: PsiElement? = element
        while (current != null && current !is PsiFile) {
            if (isCandidate(current)) return current
            current = current.parent
        }
        return null
    }

    private fun candidateChain(start: PsiElement): List<PsiElement> {
        val result = mutableListOf<PsiElement>()
        var current: PsiElement? = start
        while (current != null && current !is PsiFile) {
            if (isCandidate(current)) result.add(current)
            current = current.parent
        }
        return result
    }

    /**
     * True when the element is directly followed by another sibling element
     * (entry or value) without a comma in between.
     */
    private fun missingCommaBeforeNext(entry: PsiElement): Boolean {
        var next = entry.nextSibling
        while (next != null && next.isSkippableSibling()) next = next.nextSibling
        if (next == null || next.node?.elementType == RonTypes.COMMA) return false
        return next is RonStructEntry || next is RonMapEntry || next is RonValue
    }

    private fun entryPrecedingComma(element: PsiElement?): PsiElement? {
        if (element?.node?.elementType != RonTypes.COMMA) return null
        var prev: PsiElement? = element.prevSibling
        while (prev != null && prev.isSkippableSibling()) prev = prev.prevSibling
        return prev?.takeIf { it is RonStructEntry || it is RonMapEntry || (it is RonValue && (it.parent is RonList || it.parent is RonStructOrTuple)) }
    }

    private fun trailingCommaOf(entry: PsiElement): PsiElement? {
        var next = entry.nextSibling
        while (next != null && next.isSkippableSibling()) next = next.nextSibling
        return next?.takeIf { it.node?.elementType == RonTypes.COMMA }
    }

    private fun endOffsetSkippingTrailingWhitespace(entry: PsiElement): Int {
        var last = entry.lastChild
        while (last is PsiWhiteSpace) last = last.prevSibling ?: break
        return last?.textRange?.endOffset ?: entry.textRange.endOffset
    }

    private fun isComplete(entry: PsiElement): Boolean {
        return when (entry) {
            is RonStructEntry -> RonPsiImplUtil.getNameIdentifier(entry) != null && RonPsiImplUtil.getValue(entry) != null
            is RonMapEntry -> RonPsiImplUtil.getKey(entry) != null && RonPsiImplUtil.getValue(entry) != null
            is RonValue -> PsiTreeUtil.findChildOfType(entry, PsiErrorElement::class.java) == null
            else -> false
        }
    }

    private fun PsiElement.isSkippableSibling(): Boolean = this is PsiWhiteSpace || this is PsiComment
}
