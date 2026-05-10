package com.github.unclepomedev.ronassist.editor.folding

import com.intellij.application.options.editor.CodeFoldingOptionsProvider
import com.intellij.openapi.options.BeanConfigurable

class RonCodeFoldingOptionsProvider :
    BeanConfigurable<RonFoldingSettings>(RonFoldingSettings.instance, "RON"),
    CodeFoldingOptionsProvider {

    init {
        val settings = RonFoldingSettings.instance
        checkBox("Maps", settings::collapseMaps::get, settings::collapseMaps::set)
        checkBox("Lists", settings::collapseLists::get, settings::collapseLists::set)
        checkBox("Structs and tuples", settings::collapseStructs::get, settings::collapseStructs::set)
        checkBox("Block comments", settings::collapseBlockComments::get, settings::collapseBlockComments::set)
    }
}
