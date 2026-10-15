package com.github.unclepomedev.ronassist.psi.impl

import com.github.unclepomedev.ronassist.psi.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil

object RonPsiImplUtil {

    private val IDENTIFIER_TOKENS = TokenSet.create(RonTypes.IDENTIFIER, RonTypes.RAW_IDENTIFIER)

    /** Returns all map entries inside a RonMap, preserving source order. */
    fun getEntries(map: RonMap): List<RonMapEntry> =
        PsiTreeUtil.getChildrenOfTypeAsList(map, RonMapEntry::class.java)

    /** Returns all top-level values inside a RonList, preserving source order. */
    fun getValues(list: RonList): List<RonValue> =
        PsiTreeUtil.getChildrenOfTypeAsList(list, RonValue::class.java)

    /** Returns the leading identifier of a struct/tuple, or null for anonymous tuples. */
    fun getNameIdentifier(element: RonStructOrTuple): PsiElement? =
        element.node.findChildByType(IDENTIFIER_TOKENS)?.psi

    /** Returns all struct entries inside a RonStructOrTuple (empty for anonymous tuples). */
    fun getStructEntries(element: RonStructOrTuple): List<RonStructEntry> =
        PsiTreeUtil.getChildrenOfTypeAsList(element, RonStructEntry::class.java)

    /** Returns all anonymous tuple values inside a RonStructOrTuple (empty for named structs). */
    fun getTupleValues(element: RonStructOrTuple): List<RonValue> =
        PsiTreeUtil.getChildrenOfTypeAsList(element, RonValue::class.java)

    /** Returns the key part of a map entry (the value before the colon). */
    fun getKey(entry: RonMapEntry): RonValue? =
        PsiTreeUtil.getChildrenOfType(entry, RonValue::class.java)?.getOrNull(0)

    /** Returns the value part of a map entry (the value after the colon). */
    fun getValue(entry: RonMapEntry): RonValue? =
        PsiTreeUtil.getChildrenOfType(entry, RonValue::class.java)?.getOrNull(1)

    /** Returns the identifier of a struct entry (the name before the colon). */
    fun getNameIdentifier(entry: RonStructEntry): PsiElement? =
        entry.node.findChildByType(IDENTIFIER_TOKENS)?.psi

    /** Returns the value of a struct entry (the value after the colon). */
    fun getValue(entry: RonStructEntry): RonValue? =
        PsiTreeUtil.getChildOfType(entry, RonValue::class.java)

    /**
     * Returns the offsets (inside the element text) of the inner content of a string literal,
     * excluding surrounding quotes and any raw/byte-string `b`/`r`/`#` markers. Returns null when
     * the text is not a recognizable string form.
     */
    fun stringContentRange(text: String): TextRange? {
        if (text.isEmpty()) return null

        var offset = 0
        if (text.startsWith("b") && text.length > 1 && (text[1] == '"' || text[1] == 'r')) {
            offset = 1
        }

        val sub = text.substring(offset)
        if (sub.startsWith("r")) {
            val firstQuote = sub.indexOf('"')
            val lastQuote = sub.lastIndexOf('"')
            if (firstQuote in 1 until lastQuote) {
                return TextRange(offset + firstQuote + 1, offset + lastQuote)
            }
            return null
        }

        if (sub.length >= 2 && sub.first() == '"' && sub.last() == '"') {
            return TextRange(offset + 1, offset + sub.length - 1)
        }

        return null
    }

    /**
     * Returns the normalized identifier name for a PSI element, stripping the `r#` prefix if the
     * element's token type is [RonTypes.RAW_IDENTIFIER].
     */
    fun normalizedIdentifier(element: PsiElement): String {
        return if (element.node.elementType == RonTypes.RAW_IDENTIFIER) {
            element.text.removePrefix("r#")
        } else {
            element.text
        }
    }
}
