package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.psi.*
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement

class RonAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when {
            element.node?.elementType == RonTypes.IDENTIFIER -> {
                annotateIdentifier(element, holder)
            }
            element is RonList || element is RonMap || element is RonStructOrTuple -> {
                checkMissingCommas(element, holder)
            }
        }
    }

    /**
     * Applies semantic highlighting on top of lexer-based syntax coloring.
     * Identifier tokens that play a structural role (struct name or struct
     * field name) are recolored using dedicated text attribute keys.
     */
    private fun annotateIdentifier(element: PsiElement, holder: AnnotationHolder) {
        val attribute = attributeFor(element) ?: return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .textAttributes(attribute)
            .create()
    }

    /**
     * Returns the text attribute key for a structural identifier, or null if
     * the identifier has no special semantic role.
     */
    private fun attributeFor(element: PsiElement): TextAttributesKey? {
        val parent = element.parent ?: return null
        return when (parent) {
            is RonStructOrTuple if RonPsiImplUtil.getNameIdentifier(parent) === element ->
                RonSyntaxHighlighter.STRUCT_NAME

            is RonStructEntry if RonPsiImplUtil.getNameIdentifier(parent) === element ->
                RonSyntaxHighlighter.FIELD_NAME

            else -> null
        }
    }

    /**
     * Validates that elements inside collections (lists, maps, structs, tuples)
     * are correctly separated by commas. Generates an error annotation if a comma is missing.
     */
    private fun checkMissingCommas(element: PsiElement, holder: AnnotationHolder) {
        var child = element.firstChild
        var expectsComma = false

        while (child != null) {
            val isItem = child is RonValue || child is RonMapEntry || child is RonStructEntry
            val isComma = child.node.elementType == RonTypes.COMMA

            if (isItem) {
                if (expectsComma) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Missing comma")
                        .range(child)
                        .create()
                }
                expectsComma = true
            } else if (isComma) {
                expectsComma = false
            }

            child = child.nextSibling
        }
    }
}
