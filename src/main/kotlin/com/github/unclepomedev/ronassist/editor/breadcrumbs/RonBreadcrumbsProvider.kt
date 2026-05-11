package com.github.unclepomedev.ronassist.editor.breadcrumbs

import com.github.unclepomedev.ronassist.editor.RonPsiPresentation
import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider

class RonBreadcrumbsProvider : BreadcrumbsProvider {

    override fun getLanguages(): Array<Language> = arrayOf(RonLanguage.INSTANCE)

    /**
     * Surfaces entries and named structs/tuples as breadcrumb segments while
     * skipping anonymous containers, whose summaries add noise without
     * conveying semantic information about the user's location.
     */
    override fun acceptElement(element: PsiElement): Boolean = when (element) {
        is RonMapEntry, is RonStructEntry -> true
        is RonStructOrTuple -> RonPsiImplUtil.getNameIdentifier(element) != null
        else -> false
    }

    override fun getElementInfo(element: PsiElement): String =
        RonPsiPresentation.primaryLabel(element)
}
