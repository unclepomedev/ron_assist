package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class RonAnnotator : Annotator {

    /**
     * Applies semantic highlighting on top of lexer-based syntax coloring.
     * Identifier tokens that play a structural role (such as a struct name)
     * are recolored using dedicated text attribute keys.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.node?.elementType != RonTypes.IDENTIFIER) return

        val parent = element.parent ?: return
        if (parent is RonStructOrTuple &&
            RonPsiImplUtil.getNameIdentifier(parent) === element
        ) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .textAttributes(RonSyntaxHighlighter.STRUCT_NAME)
                .create()
        }
    }
}