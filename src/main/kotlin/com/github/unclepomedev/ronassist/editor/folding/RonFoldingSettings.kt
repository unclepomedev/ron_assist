package com.github.unclepomedev.ronassist.editor.folding

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(name = "RonFoldingSettings", storages = [Storage("ron_assist.xml")])
class RonFoldingSettings : PersistentStateComponent<RonFoldingSettings> {

    var collapseMaps: Boolean = false
    var collapseLists: Boolean = false
    var collapseStructs: Boolean = false
    var collapseBlockComments: Boolean = false

    override fun getState(): RonFoldingSettings = this

    override fun loadState(state: RonFoldingSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: RonFoldingSettings
            get() = ApplicationManager.getApplication().getService(RonFoldingSettings::class.java)
    }
}
