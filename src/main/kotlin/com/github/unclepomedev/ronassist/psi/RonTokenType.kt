package com.github.unclepomedev.ronassist.psi

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.psi.tree.IElementType

class RonTokenType(debugName: String) : IElementType(debugName, RonLanguage.INSTANCE) {
    override fun toString(): String = "RonTokenType." + super.toString()
}
