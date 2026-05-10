package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.formatting.*
import com.intellij.psi.codeStyle.CodeStyleSettings

class RonFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val commonSettings = settings.getCommonSettings(RonLanguage.INSTANCE)

        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1

        val spacingBuilder = createSpacingBuilder(settings)
        val block = RonBlock(formattingContext.node, null, null, spacingBuilder)

        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            block,
            settings
        )
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(settings, RonLanguage.INSTANCE)
            .before(RonTypes.COLON).spaceIf(false)
            .after(RonTypes.COLON).spaceIf(true)
            .before(RonTypes.COMMA).spaceIf(false)
            .after(RonTypes.COMMA).spaceIf(true)
            .withinPair(RonTypes.LBRACK, RonTypes.RBRACK).spaceIf(false)
            .withinPair(RonTypes.LBRACE, RonTypes.RBRACE).spaceIf(false)
            .withinPair(RonTypes.LPAREN, RonTypes.RPAREN).spaceIf(false)
    }
}
