package com.github.unclepomedev.ronassist.inspection

import com.github.unclepomedev.ronassist.psi.RonMap
import com.github.unclepomedev.ronassist.psi.RonStringVal
import com.github.unclepomedev.ronassist.psi.RonValue
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class RonDuplicateMapKeyInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : PsiElementVisitor() {

            /**
             * Inspects each RonMap in the file and reports map entries whose key has already been
             * seen in the same map, leaving the first occurrence untouched.
             */
            override fun visitElement(element: PsiElement) {
                if (element !is RonMap) return

                val seen = HashSet<String>()
                for (entry in RonPsiImplUtil.getEntries(element)) {
                    val key = RonPsiImplUtil.getKey(entry) ?: continue
                    val canonical = canonicalKey(key)
                    if (!seen.add(canonical)) {
                        holder.registerProblem(
                            key,
                            RonInspectionBundle.message("inspection.map.duplicate.key.problem"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        )
                    }
                }
            }
        }

    /**
     * Computes a canonical key string so that equivalent literals collapse to the same form.
     * Currently normalizes standard and raw strings; escape sequences and numeric bases are
     * intentionally not normalized.
     */
    private fun canonicalKey(key: RonValue): String {
        return when (val child = key.firstChild) {
            is RonStringVal -> "str:${stripStringQuotes(child.text)}"
            else -> "raw:${key.text}"
        }
    }

    /** Strips surrounding quotes and any raw-string `r`/`#` markers. */
    private fun stripStringQuotes(text: String): String {
        val range = RonPsiImplUtil.stringContentRange(text) ?: return text
        return range.substring(text)
    }
}
