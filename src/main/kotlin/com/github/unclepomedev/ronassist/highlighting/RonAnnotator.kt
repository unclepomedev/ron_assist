package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement

class RonAnnotator : Annotator {

    /**
     * Applies semantic highlighting on top of lexer-based syntax coloring.
     * Identifier tokens that play a structural role (struct name or struct
     * field name) are recolored using dedicated text attribute keys.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.node?.elementType != RonTypes.IDENTIFIER) return

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
}
