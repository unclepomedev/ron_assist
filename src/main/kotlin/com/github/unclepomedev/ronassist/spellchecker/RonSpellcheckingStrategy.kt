package com.github.unclepomedev.ronassist.spellchecker

import com.github.unclepomedev.ronassist.psi.RonStringVal
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer

class RonSpellcheckingStrategy : SpellcheckingStrategy() {

    /**
     * Routes RON elements to appropriate tokenizers: string literals and
     * identifiers (struct names, field names) go through text-based
     * spellchecking; comments use the platform's comment tokenizer.
     */
    override fun getTokenizer(element: PsiElement): Tokenizer<*> {
        if (element is PsiComment) return myCommentTokenizer
        if (element is RonStringVal) return RonStringTokenizer
        if (element.node?.elementType == RonTypes.IDENTIFIER) {
            val parent = element.parent
            if (parent is RonStructOrTuple || parent is RonStructEntry) {
                return TEXT_TOKENIZER
            }
        }
        return super.getTokenizer(element)
    }
}
