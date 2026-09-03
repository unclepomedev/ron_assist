package com.github.unclepomedev.ronassist.icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object RonIcons {
    @JvmField val FILE: Icon = IconLoader.getIcon("/icons/ron.svg", RonIcons::class.java)

    /** Structure view icons (reusing platform icons for now). */
    val MAP: Icon = AllIcons.Json.Object
    val LIST: Icon = AllIcons.Json.Array
    val STRUCT: Icon = AllIcons.Nodes.Class
    val PROPERTY: Icon = AllIcons.Nodes.Property
}
