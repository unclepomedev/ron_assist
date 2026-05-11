package com.github.unclepomedev.ronassist.editor.structureview

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
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.pom.Navigatable
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class RonStructureViewElement(
    private val element: PsiElement,
) : StructureViewTreeElement, Navigatable {

    override fun getValue(): Any = element

    override fun navigate(requestFocus: Boolean) {
        (element as? NavigatablePsiElement)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean =
        (element as? NavigatablePsiElement)?.canNavigate() == true

    override fun canNavigateToSource(): Boolean =
        (element as? NavigatablePsiElement)?.canNavigateToSource() == true

    override fun getPresentation(): ItemPresentation =
        PresentationData(labelFor(element), null, null, null)

    override fun getChildren(): Array<TreeElement> = collectChildren(element)
        .map { RonStructureViewElement(it) }
        .toTypedArray()

    /**
     * Returns the displayable children of this node. Currently expands the file
     * root and one level of map/struct entries; nested containers are not yet
     * traversed.
     */
    private fun collectChildren(element: PsiElement): List<PsiElement> = when (element) {
        is RonFile -> {
            val root = PsiTreeUtil.getChildOfType(element, RonValue::class.java)
            if (root != null) listOfNotNull(unwrapContainer(root)) else emptyList()
        }
        is RonMap -> RonPsiImplUtil.getEntries(element)
        is RonStructOrTuple -> RonPsiImplUtil.getStructEntries(element)
        else -> emptyList()
    }

    /**
     * Returns the concrete container (map/list/struct) inside a RonValue,
     * or null when the value is a primitive.
     */
    private fun unwrapContainer(value: RonValue): PsiElement? {
        return when (val child = value.firstChild) {
            is RonMap, is RonList, is RonStructOrTuple -> child
            else -> null
        }
    }

    /** Temporary label generator; will be replaced by a shared helper in phase 5. */
    private fun labelFor(element: PsiElement): String = when (element) {
        is RonFile -> element.toString()
        is RonMap -> "{...}"
        is RonList -> "[...]"
        is RonStructOrTuple -> "(...)"
        is RonMapEntry -> {
            val key = RonPsiImplUtil.getKey(element)
            key?.text?.take(40) ?: "<key>"
        }
        is RonStructEntry -> {
            RonPsiImplUtil.getNameIdentifier(element)?.text ?: "<entry>"
        }
        else -> element.text.take(40)
    }
}