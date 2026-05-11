package com.github.unclepomedev.ronassist.editor.structureview

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.pom.Navigatable
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement

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
        PresentationData(element.toString(), null, null, null)

    /** TODO: implement here next */
    override fun getChildren(): Array<TreeElement> = emptyArray()
}