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
 * Shared presentation helpers for displaying RON PSI elements in trees,
 * breadcrumbs, and other UI surfaces.
 */
object RonPsiPresentation {

    private const val LITERAL_PREVIEW_LIMIT = 40

    /**
     * Primary label shown in the tree row or breadcrumb segment. Containers
     * show a compact summary; entries show their key/identifier.
     */
    fun primaryLabel(element: PsiElement): String = when (element) {
        is RonMap -> "{${RonPsiImplUtil.getEntries(element).size}}"
        is RonList -> "[${RonPsiImplUtil.getValues(element).size}]"
        is RonStructOrTuple -> {
            val name = RonPsiImplUtil.getNameIdentifier(element)?.text
            if (name != null) "$name(...)" else "(...)"
        }
        is RonMapEntry -> renderMapKey(element)
        is RonStructEntry -> RonPsiImplUtil.getNameIdentifier(element)?.text ?: "<entry>"
        else -> element.text.take(LITERAL_PREVIEW_LIMIT)
    }

    /**
     * Trailing preview shown next to the primary label, typically the value
     * of an entry. Returns null when there is nothing useful to show.
     */
    fun locationPreview(element: PsiElement): String? = when (element) {
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
     * Compact one-line preview of a RonValue. Containers collapse to their
     * structural summary; primitives keep their literal form (truncated).
     */
    fun previewValue(value: RonValue): String {
        val child = value.firstChild ?: return value.text.take(LITERAL_PREVIEW_LIMIT)
        return when (child) {
            is RonStringVal -> child.text.take(LITERAL_PREVIEW_LIMIT)
            is RonCharVal -> child.text
            is RonNumeric -> child.text
            is RonBoolean -> child.text
            is RonOption -> child.text.take(LITERAL_PREVIEW_LIMIT)
            is RonMap -> "{${RonPsiImplUtil.getEntries(child).size}}"
            is RonList -> "[${RonPsiImplUtil.getValues(child).size}]"
            is RonStructOrTuple -> {
                val name = RonPsiImplUtil.getNameIdentifier(child)?.text
                if (name != null) "$name(...)" else "(...)"
            }
            else -> child.text.take(LITERAL_PREVIEW_LIMIT)
        }
    }
}
