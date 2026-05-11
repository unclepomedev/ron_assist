package com.github.unclepomedev.ronassist.inspection

import com.github.unclepomedev.ronassist.psi.RonMap
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
    ): PsiElementVisitor = object : PsiElementVisitor() {

        /**
         * Inspects each RonMap in the file and reports map entries whose key
         * has already been seen in the same map, leaving the first occurrence
         * untouched.
         */
        override fun visitElement(element: PsiElement) {
            if (element !is RonMap) return

            val seen = HashSet<String>()
            for (entry in RonPsiImplUtil.getEntries(element)) {
                val key = RonPsiImplUtil.getKey(entry) ?: continue
                val keyText = key.text
                if (!seen.add(keyText)) {
                    holder.registerProblem(
                        key,
                        RonInspectionBundle.message("inspection.map.duplicate.key.problem"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    )
                }
            }
        }
    }
}
