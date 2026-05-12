package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.psi.tree.TokenSet

class RonQuoteHandler : SimpleTokenSetQuoteHandler(
    TokenSet.create(
        RonTypes.STRING,
        RonTypes.RAW_STRING,
        RonTypes.CHAR,
    ),
)
