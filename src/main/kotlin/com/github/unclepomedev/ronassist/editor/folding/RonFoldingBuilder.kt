package com.github.unclepomedev.ronassist.editor.folding

import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class RonFoldingBuilder : CustomFoldingBuilder(), DumbAware {

    /**
     * Walks the PSI tree and registers fold regions for multi-line collections,
     * structs/tuples, and block comments.
     */
    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        PsiTreeUtil.processElements(root) { element ->
            when {
                element is RonMap ||
                        element is RonList ||
                        element is RonStructOrTuple ->
                    addFoldIfMultiline(element, document, descriptors)

                element.node.elementType == RonTypes.BLOCK_COMMENT ->
                    addFoldIfMultiline(element, document, descriptors)
            }
            true
        }
    }

    /**
     * Registers a fold descriptor only when the element spans multiple lines,
     * using cheap document line-number comparison to avoid scanning element text.
     */
    private fun addFoldIfMultiline(
        element: PsiElement,
        document: Document,
        descriptors: MutableList<FoldingDescriptor>
    ) {
        val range = element.textRange
        if (range.length <= 2) return
        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber(range.endOffset)
        if (endLine <= startLine) return
        descriptors.add(FoldingDescriptor(element.node, range))
    }

    /**
     * Returns a concise placeholder summarizing the folded element, using element
     * counts for collections and the identifier (if any) for structs and tuples.
     */
    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        return when (val psi = node.psi) {
            is RonMap -> "{${RonPsiImplUtil.getEntries(psi).size}}"
            is RonList -> "[${RonPsiImplUtil.getValues(psi).size}]"
            is RonStructOrTuple -> {
                val name = RonPsiImplUtil.getNameIdentifier(psi)?.text
                if (name != null) "$name(...)" else "(...)"
            }

            else -> when (node.elementType) {
                RonTypes.BLOCK_COMMENT -> "/* ... */"
                else -> "..."
            }
        }
    }

    /**
     * Determines whether a fold region should start collapsed based on user
     * preferences stored in RonFoldingSettings.
     */
    override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
        val settings = RonFoldingSettings.instance
        return when (node.psi) {
            is RonMap -> settings.collapseMaps
            is RonList -> settings.collapseLists
            is RonStructOrTuple -> settings.collapseStructs
            else -> when (node.elementType) {
                RonTypes.BLOCK_COMMENT -> settings.collapseBlockComments
                else -> false
            }
        }
    }
}
