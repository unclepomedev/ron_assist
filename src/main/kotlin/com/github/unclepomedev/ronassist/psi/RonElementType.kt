package com.github.unclepomedev.ronassist.psi

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.psi.tree.IElementType

class RonElementType(debugName: String) : IElementType(debugName, RonLanguage.INSTANCE)
