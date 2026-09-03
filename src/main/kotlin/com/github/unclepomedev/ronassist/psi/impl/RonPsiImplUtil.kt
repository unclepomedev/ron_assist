package com.github.unclepomedev.ronassist.psi.impl

import com.github.unclepomedev.ronassist.psi.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

object RonPsiImplUtil {

    /** Returns all map entries inside a RonMap, preserving source order. */
    fun getEntries(map: RonMap): List<RonMapEntry> =
        PsiTreeUtil.getChildrenOfTypeAsList(map, RonMapEntry::class.java)

    /** Returns all top-level values inside a RonList, preserving source order. */
    fun getValues(list: RonList): List<RonValue> =
        PsiTreeUtil.getChildrenOfTypeAsList(list, RonValue::class.java)

    /** Returns the leading identifier of a struct/tuple, or null for anonymous tuples. */
    fun getNameIdentifier(element: RonStructOrTuple): PsiElement? =
        element.node.findChildByType(RonTypes.IDENTIFIER)?.psi

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
        entry.node.findChildByType(RonTypes.IDENTIFIER)?.psi

    /** Returns the value of a struct entry (the value after the colon). */
    fun getValue(entry: RonStructEntry): RonValue? =
        PsiTreeUtil.getChildOfType(entry, RonValue::class.java)

    /**
     * Returns the offsets (inside the element text) of the inner content of a string literal,
     * excluding surrounding quotes and any raw-string `r`/`#` markers. Returns null when the text
     * is not a recognizable string form.
     */
    fun stringContentRange(text: String): TextRange? {
        if (text.isEmpty()) return null

        if (text.startsWith("r")) {
            val firstQuote = text.indexOf('"')
            val lastQuote = text.lastIndexOf('"')
            if (firstQuote in 1 until lastQuote) {
                return TextRange(firstQuote + 1, lastQuote)
            }
            return null
        }

        if (text.length >= 2 && text.first() == '"' && text.last() == '"') {
            return TextRange(1, text.length - 1)
        }

        return null
    }
}
