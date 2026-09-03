package com.github.unclepomedev.ronassist.spellchecker

import com.github.unclepomedev.ronassist.psi.RonStringVal
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer

/**
 * Spellchecks the inner content of a RON string literal, excluding the surrounding quotes and any
 * raw-string `r`/`#` delimiters.
 */
object RonStringTokenizer : Tokenizer<RonStringVal>() {

    override fun tokenize(element: RonStringVal, consumer: TokenConsumer) {
        val text = element.text
        val range = RonPsiImplUtil.stringContentRange(text) ?: return
        consumer.consumeToken(element, text, false, 0, range, PlainTextSplitter.getInstance())
    }
}
