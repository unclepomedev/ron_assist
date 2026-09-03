package com.github.unclepomedev.ronassist.editor

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesDelegate

/** Enable EnterBetweenBraces behavior for all RON parenthesis pairs (() [] {}). */
class RonEnterBetweenBracesDelegate : EnterBetweenBracesDelegate() {
    override fun isBracePair(lBrace: Char, rBrace: Char): Boolean {
        // The standard specification only handles {} pairs, so explicitly handling all pairs.
        return (lBrace == '(' && rBrace == ')') ||
            (lBrace == '[' && rBrace == ']') ||
            (lBrace == '{' && rBrace == '}')
    }
}
