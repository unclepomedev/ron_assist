package com.github.unclepomedev.ronassist.inspection

import com.github.unclepomedev.ronassist.psi.RonStructOrTuple
import com.github.unclepomedev.ronassist.psi.impl.RonPsiImplUtil
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class RonDuplicateStructFieldInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor = object : PsiElementVisitor() {

        /**
         * Inspects each RonStructOrTuple in the file and reports struct entries
         * whose field name has already been seen in the same struct, leaving
         * the first occurrence untouched. Anonymous tuples are skipped because
         * they have no named fields.
         */
        override fun visitElement(element: PsiElement) {
            if (element !is RonStructOrTuple) return

            val seen = HashSet<String>()
            for (entry in RonPsiImplUtil.getStructEntries(element)) {
                val nameIdentifier = RonPsiImplUtil.getNameIdentifier(entry) ?: continue
                val name = nameIdentifier.text
                if (!seen.add(name)) {
                    holder.registerProblem(
                        nameIdentifier,
                        RonInspectionBundle.message("inspection.struct.duplicate.field.problem"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    )
                }
            }
        }
    }
}
