package com.github.unclepomedev.ronassist.livetemplates

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class RonTemplateContextType : TemplateContextType("RON") {

    /**
     * Returns true when the template should be available at the cursor position. We enable RON
     * templates for any file written in the RON language.
     */
    override fun isInContext(context: TemplateActionContext): Boolean =
        context.file.language.isKindOf(RonLanguage.INSTANCE)
}
