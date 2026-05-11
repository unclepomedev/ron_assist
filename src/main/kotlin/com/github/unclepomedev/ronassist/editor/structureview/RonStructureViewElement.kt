package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.editor.RonPsiPresentation
import com.github.unclepomedev.ronassist.icons.RonIcons
import com.github.unclepomedev.ronassist.parser.RonFile
import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonValue
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.pom.Navigatable
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.Locale
import javax.swing.Icon

class RonStructureViewElement(
    private val element: PsiElement,
) : StructureViewTreeElement, SortableTreeElement, Navigatable {

    override fun getValue(): Any = element

    override fun navigate(requestFocus: Boolean) {
        (element as? NavigatablePsiElement)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean =
        (element as? NavigatablePsiElement)?.canNavigate() == true

    override fun canNavigateToSource(): Boolean =
        (element as? NavigatablePsiElement)?.canNavigateToSource() == true

    /**
     * Returns the key used by the alphabetical sorter. We strip surrounding
     * quotes from string map keys so that "alpha" sorts naturally next to a
     * bare identifier alpha rather than under the quote character.
     */
    override fun getAlphaSortKey(): String {
        val label = RonPsiPresentation.primaryLabel(element)
        return label.removeSurrounding("\"").lowercase(Locale.ROOT)
    }

    override fun getPresentation(): ItemPresentation {
        val label = if (element is RonFile) element.toString()
        else RonPsiPresentation.primaryLabel(element)
        val location = RonPsiPresentation.locationPreview(element)
        return PresentationData(label, location, iconFor(element), null)
    }

    override fun getChildren(): Array<TreeElement> = collectChildren(element)
        .map { RonStructureViewElement(it) }
        .toTypedArray()

    /**
     * Returns the displayable children of a node, recursing into nested
     * containers while skipping primitive values that have no further structure.
     */
    private fun collectChildren(element: PsiElement): List<PsiElement> = when (element) {
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
        is RonMapEntry -> listOfNotNull(RonPsiImplUtil.getValue(element)?.let { unwrapContainer(it) })
        is RonStructEntry -> listOfNotNull(RonPsiImplUtil.getValue(element)?.let { unwrapContainer(it) })
        else -> emptyList()
    }

    /**
     * Returns the concrete container (map/list/struct) inside a RonValue,
     * or null when the value is a primitive that has no further structure.
     */
    private fun unwrapContainer(value: RonValue): PsiElement? {
        return when (val child = value.firstChild) {
            is RonMap, is RonList, is RonStructOrTuple -> child
            else -> null
        }
    }

    private fun iconFor(element: PsiElement): Icon? = when (element) {
        is RonFile -> RonIcons.FILE
        is RonMap -> RonIcons.MAP
        is RonList -> RonIcons.LIST
        is RonStructOrTuple -> RonIcons.STRUCT
        is RonMapEntry, is RonStructEntry -> RonIcons.PROPERTY
        else -> null
    }
}
