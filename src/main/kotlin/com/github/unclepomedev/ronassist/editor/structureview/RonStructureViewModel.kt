package com.github.unclepomedev.ronassist.editor.structureview

import com.github.unclepomedev.ronassist.parser.RonFile
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor

class RonStructureViewModel(editor: Editor?, file: RonFile) :
    StructureViewModelBase(file, editor, RonStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider {

    init {
        withSorters(Sorter.ALPHA_SORTER)
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean = false
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false
}
