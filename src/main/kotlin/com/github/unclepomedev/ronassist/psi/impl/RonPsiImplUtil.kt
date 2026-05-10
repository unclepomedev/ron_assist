package com.github.unclepomedev.ronassist.psi.impl

import com.github.unclepomedev.ronassist.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

object RonPsiImplUtil {

    /** Returns all map entries inside a RonMap, preserving source order. */
    fun getEntries(map: RonMap): List<RonMapEntry> =
        PsiTreeUtil.getChildrenOfTypeAsList(map, RonMapEntry::class.java)

    /** Returns all top-level values inside a RonList, preserving source order. */
    fun getValues(list: RonList): List<RonValue> =
        PsiTreeUtil.getChildrenOfTypeAsList(list, RonValue::class.java)

    /** Returns the leading identifier of a struct/tuple, or null for anonymous tuples. */
    fun getNameIdentifier(element: RonStructOrTuple): PsiElement? =
        element.node.findChildByType(RonTypes.IDENTIFIER)?.psi
}
