package com.github.unclepomedev.ronassist.editor.folding

import com.intellij.application.options.editor.CodeFoldingOptionsProvider
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel

class RonCodeFoldingOptionsProvider :
    BoundConfigurable("RON"),
    CodeFoldingOptionsProvider,
    SearchableConfigurable {

    override fun getId(): String = "editor.preferences.folding.ron"

    override fun createPanel() = panel {
        val settings = RonFoldingSettings.instance
        group("RON") {
            row {
                checkBox("Maps")
                    .bindSelected(settings::collapseMaps)
            }
            row {
                checkBox("Lists")
                    .bindSelected(settings::collapseLists)
            }
            row {
                checkBox("Structs and tuples")
                    .bindSelected(settings::collapseStructs)
            }
            row {
                checkBox("Block comments")
                    .bindSelected(settings::collapseBlockComments)
            }
        }
    }
}
