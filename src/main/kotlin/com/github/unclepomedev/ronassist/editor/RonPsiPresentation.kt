package com.github.unclepomedev.ronassist.editor

import com.github.unclepomedev.ronassist.psi.RonBoolean
import com.github.unclepomedev.ronassist.psi.RonCharVal
import com.github.unclepomedev.ronassist.psi.RonList
import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonMapEntry
import com.github.unclepomedev.ronassist.psi.RonNumeric
import com.github.unclepomedev.ronassist.psi.RonOption
import com.github.unclepomedev.ronassist.psi.RonStringVal
import com.github.unclepomedev.ronassist.psi.RonStructEntry
import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.RonValue
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.psi.PsiElement

/**
 * Shared presentation helpers for displaying RON PSI elements in trees, breadcrumbs, and other UI
 * surfaces.
 */
object RonPsiPresentation {

    private const val LITERAL_PREVIEW_LIMIT = 40

    /**
     * Primary label shown in the tree row or breadcrumb segment. Containers show a compact summary;
     * entries show their key/identifier.
     */
    fun primaryLabel(element: PsiElement): String =
        when (element) {
            is RonMap -> "{${RonPsiImplUtil.getEntries(element).size}}"
            is RonList -> "[${RonPsiImplUtil.getValues(element).size}]"
            is RonStructOrTuple -> {
                val name = RonPsiImplUtil.getNameIdentifier(element)?.text
                if (name != null) "$name(...)" else "(...)"
            }
            is RonMapEntry -> renderMapKey(element)
            is RonStructEntry -> RonPsiImplUtil.getNameIdentifier(element)?.text ?: "<entry>"
            else -> oneLinePreview(element.text)
        }

    /**
     * Trailing preview shown next to the primary label, typically the value of an entry. Returns
     * null when there is nothing useful to show.
     */
    fun locationPreview(element: PsiElement): String? =
        when (element) {
            is RonMapEntry -> RonPsiImplUtil.getValue(element)?.let { "= ${previewValue(it)}" }
            is RonStructEntry -> RonPsiImplUtil.getValue(element)?.let { "= ${previewValue(it)}" }
            else -> null
        }

    /** Renders a map entry's key, preferring a literal preview over raw text. */
    private fun renderMapKey(entry: RonMapEntry): String {
        val key = RonPsiImplUtil.getKey(entry) ?: return "<key>"
        return previewValue(key)
    }

    /**
     * Compact one-line preview of a RonValue. Containers collapse to their structural summary;
     * primitives keep their literal form (truncated).
     */
    fun previewValue(value: RonValue): String {
        val child = value.firstChild ?: return oneLinePreview(value.text)
        return when (child) {
            is RonStringVal -> oneLinePreview(child.text)
            is RonCharVal -> child.text
            is RonNumeric -> child.text
            is RonBoolean -> child.text
            is RonOption -> oneLinePreview(child.text)
            is RonMap -> "{${RonPsiImplUtil.getEntries(child).size}}"
            is RonList -> "[${RonPsiImplUtil.getValues(child).size}]"
            is RonStructOrTuple -> {
                val name = RonPsiImplUtil.getNameIdentifier(child)?.text
                if (name != null) "$name(...)" else "(...)"
            }
            else -> oneLinePreview(child.text)
        }
    }

    /**
     * Builds a compact one-line preview by collapsing internal whitespace runs (including newlines)
     * into single spaces and truncating to LITERAL_PREVIEW_LIMIT characters.
     *
     * Leading and trailing whitespace are stripped. This is appropriate for label-style previews
     * where surrounding whitespace carries no information, but callers that need to preserve
     * leading whitespace should use a different formatter.
     *
     * The traversal is bounded by the preview limit, so very long inputs (such as multi-line
     * RAW_STRING values) are processed in O(limit) time.
     */
    private fun oneLinePreview(text: String): String {
        if (text.isEmpty()) return text

        val sb = StringBuilder(LITERAL_PREVIEW_LIMIT)
        var lastWasSpace = true
        for (i in text.indices) {
            val c = text[i]
            if (c.isWhitespace()) {
                if (!lastWasSpace) {
                    sb.append(' ')
                    lastWasSpace = true
                }
            } else {
                sb.append(c)
                lastWasSpace = false
            }
            if (sb.length >= LITERAL_PREVIEW_LIMIT) break
        }
        if (sb.isNotEmpty() && sb.last() == ' ') sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }

    /**
     * Returns a tooltip string suitable for hover-over UI such as breadcrumbs. Returns null when no
     * useful detail can be shown beyond the primary label.
     */
    fun tooltipFor(element: PsiElement): String? =
        when (element) {
            is RonMapEntry -> {
                val preview =
                    RonPsiImplUtil.getValue(element)?.let { previewValue(it) } ?: return null
                "${primaryLabel(element)} = $preview"
            }
            is RonStructEntry -> {
                val preview =
                    RonPsiImplUtil.getValue(element)?.let { previewValue(it) } ?: return null
                "${primaryLabel(element)} = $preview"
            }
            is RonStructOrTuple -> {
                val name = RonPsiImplUtil.getNameIdentifier(element)?.text ?: return null
                val fieldCount = RonPsiImplUtil.getStructEntries(element).size
                val tupleCount = RonPsiImplUtil.getTupleValues(element).size
                when {
                    fieldCount > 0 -> "$name with $fieldCount ${pluralize("field", fieldCount)}"
                    tupleCount > 0 -> "$name with $tupleCount ${pluralize("value", tupleCount)}"
                    else -> "$name (empty)"
                }
            }
            else -> null
        }

    private fun pluralize(word: String, count: Int): String = if (count == 1) word else "${word}s"
}
