package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.editor.RonPsiPresentation
import com.github.unclepomedev.ronassist.icons.RonIcons
import com.github.unclepomedev.ronassist.parser.RonFile
import com.github.unclepomedev.ronassist.psi.*
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

class RonStructureViewElement(psi: PsiElement) :
    PsiTreeElementBase<PsiElement>(psi), SortableTreeElement {
    /**
     * Returns the key used by the alphabetical sorter. Strips surrounding quotes or string markers
     * from string map keys and any `r#` prefix from raw identifiers so that they sort naturally
     * next to their bare equivalents rather than under quote characters or prefixes.
     */
    override fun getAlphaSortKey(): String {
        val psi = element ?: return ""
        return RonPsiPresentation.sortKey(psi)
    }

    override fun getPresentableText(): String {
        val psi = element ?: return ""
        return if (psi is RonFile) psi.toString() else RonPsiPresentation.primaryLabel(psi)
    }

    override fun getLocationString(): String? = element?.let {
        RonPsiPresentation.locationPreview(it)
    }

    override fun getIcon(open: Boolean): Icon? = element?.let { iconFor(it) }

    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val psi = element ?: return emptyList()
        return collectChildren(psi).map { RonStructureViewElement(it) }
    }

    /**
     * Returns the displayable children of a node, recursing into nested containers while skipping
     * primitive values that have no further structure.
     */
    private fun collectChildren(element: PsiElement): List<PsiElement> =
        when (element) {
            is RonFile -> {
                val root = PsiTreeUtil.getChildOfType(element, RonValue::class.java)
                if (root != null) listOfNotNull(unwrapContainer(root)) else emptyList()
            }
            is RonMap -> RonPsiImplUtil.getEntries(element)
            is RonStructOrTuple -> {
                val structEntries = RonPsiImplUtil.getStructEntries(element)
                structEntries.ifEmpty {
                    RonPsiImplUtil.getTupleValues(element).mapNotNull { unwrapContainer(it) }
                }
            }
            is RonList -> RonPsiImplUtil.getValues(element).mapNotNull { unwrapContainer(it) }
            is RonMapEntry ->
                listOfNotNull(RonPsiImplUtil.getValue(element)?.let { unwrapContainer(it) })
            is RonStructEntry ->
                listOfNotNull(RonPsiImplUtil.getValue(element)?.let { unwrapContainer(it) })
            else -> emptyList()
        }

    /**
     * Returns the concrete container (map/list/struct) inside a RonValue, or null when the value is
     * a primitive that has no further structure.
     */
    private fun unwrapContainer(value: RonValue): PsiElement? {
        return when (val child = value.firstChild) {
            is RonMap,
            is RonList,
            is RonStructOrTuple -> child
            else -> null
        }
    }

    private fun iconFor(element: PsiElement): Icon? =
        when (element) {
            is RonFile -> RonIcons.FILE
            is RonMap -> RonIcons.MAP
            is RonList -> RonIcons.LIST
            is RonStructOrTuple -> RonIcons.STRUCT
            is RonMapEntry,
            is RonStructEntry -> RonIcons.PROPERTY
            else -> null
        }
}
