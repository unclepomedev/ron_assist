package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class RonBraceMatcher : PairedBraceMatcher {

    /**
     * Declares the matched brace pairs in RON, with `structural=true` so that
     * the IDE treats each pair as a navigation/scope boundary.
     */
    override fun getPairs(): Array<BracePair> = PAIRS

    /**
     * Allows inserting a closing brace before any token, so completion can
     * pair-match `(` even when followed by an identifier or value.
     */
    override fun isPairedBracesAllowedBeforeType(
        lbraceType: IElementType,
        contextType: IElementType?,
    ): Boolean = true

    /** Pair lookups start from the brace token itself, so no offset adjustment. */
    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int =
        openingBraceOffset

    companion object {
        private val PAIRS = arrayOf(
            BracePair(RonTypes.LBRACE, RonTypes.RBRACE, true),
            BracePair(RonTypes.LBRACK, RonTypes.RBRACK, true),
            BracePair(RonTypes.LPAREN, RonTypes.RPAREN, true),
        )
    }
}
