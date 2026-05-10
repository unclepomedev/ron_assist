package com.github.unclepomedev.ronassist.lexer

import com.intellij.lexer.FlexAdapter

class RonLexerAdapter : FlexAdapter(RonLexer(null))
