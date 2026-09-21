package com.github.unclepomedev.ronassist.completion

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.github.unclepomedev.ronassist.psi.*
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil

class RonSchemaCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(
        parameters: CompletionParameters,
        result: CompletionResultSet,
    ) {
        val originalPosition = parameters.position
        if (originalPosition.node.elementType != RonTypes.IDENTIFIER) return

        val (file, position) =
            reparseWithClosedRootStruct(originalPosition.containingFile, originalPosition)
                ?: (originalPosition.containingFile to originalPosition)

        val target = findRootStructFieldTarget(file, position) ?: return
        val typePath = findTypePath(file) ?: return
        val fields = file.project.service<RonSchemaService>().fields(typePath) ?: return

        val existing =
            target.root.structEntryList
                .filter { it != target.entry }
                .map { it.identifier.text }
                .toSet()
        for (field in fields) {
            if (field !in existing) result.addElement(LookupElementBuilder.create(field))
        }
    }

    private data class RootStructFieldTarget(
        val root: RonStructOrTuple,
        val entry: RonStructEntry?,
    )

    private fun findRootStructFieldTarget(
        file: PsiFile,
        position: PsiElement,
    ): RootStructFieldTarget? {
        val root =
            file.children.filterIsInstance<RonValue>().singleOrNull()?.structOrTuple ?: return null
        val struct =
            PsiTreeUtil.getParentOfType(position, RonStructOrTuple::class.java) ?: return null
        if (struct != root || root.identifier == position) return null

        val entry = PsiTreeUtil.getParentOfType(position, RonStructEntry::class.java)
        if (entry != null) {
            if (entry.parent != root || entry.identifier != position) return null
        } else {
            val value = position.parent as? RonValue ?: return null
            if (value.parent != root || value.identifier != position) return null
            if (root.valueList.any { it != value }) return null
        }
        return RootStructFieldTarget(root, entry)
    }

    private fun findTypePath(file: PsiFile): String? {
        val attributes = file.children.filterIsInstance<RonAttribute>()
        if (
            attributes.any { PsiTreeUtil.findChildOfType(it, PsiErrorElement::class.java) != null }
        ) {
            return null
        }
        val attribute = attributes.singleOrNull { it.identifier?.text == "type" } ?: return null
        return RonStringLiteral.unescape(attribute.stringVal?.text)
    }

    /**
     * Attempts to complete an unclosed top-level struct by appending a closing parenthesis `)`.
     *
     * FIXME: This hack relies on the grammar assumption that an unclosed top-level struct (e.g.
     * `#![type = "T"]\n(\nhost:`) becomes syntactically valid if closed with `)`. This workaround
     * should ideally be replaced by improving the parser recovery rules in Ron.bnf so that
     * incomplete structs retain proper PSI structure during editing without needing synthetic
     * re-parsing. Note: Formatter, inspections, and other IDE features may also suffer from this
     * parser limitation.
     */
    private fun reparseWithClosedRootStruct(
        file: PsiFile,
        position: PsiElement,
    ): Pair<PsiFile, PsiElement>? {
        val hasNoRootStruct =
            file.children.filterIsInstance<RonValue>().singleOrNull()?.structOrTuple == null
        val hasAttribute =
            file.node.getChildren(null).any { it.elementType == RonTypes.ATTRIBUTE_START }
        val isAtEndOfFile = file.text.substring(position.textRange.endOffset).isBlank()

        if (hasNoRootStruct && hasAttribute && isAtEndOfFile) {
            val reparsedFile =
                PsiFileFactory.getInstance(file.project)
                    .createFileFromText("completion.ron", RON_FILE_TYPE, file.text + ")")
            val reparsedPosition =
                reparsedFile.findElementAt(position.textRange.startOffset) ?: return null
            return reparsedFile to reparsedPosition
        }
        return null
    }
}
