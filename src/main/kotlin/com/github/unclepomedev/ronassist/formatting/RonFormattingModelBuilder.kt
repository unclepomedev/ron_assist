package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock

class RonFormattingModelBuilder : FormattingModelBuilder {

    /**
     * Creates the core formatting model for a RON file, initializing spacing rules and block
     * hierarchies.
     */
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val file = formattingContext.containingFile

        val block: Block =
            if (InjectedLanguageManager.getInstance(file.project).isInjectedFragment(file)) {
                NoopBlock(formattingContext.node)
            } else {
                val spacingBuilder = createSpacingBuilder(settings)
                RonBlock(formattingContext.node, null, null, spacingBuilder)
            }

        return FormattingModelProvider.createFormattingModelForPsiFile(file, block, settings)
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
        return SpacingBuilder(settings, RonLanguage.INSTANCE)
            .before(RonTypes.COLON)
            .spaceIf(false)
            .after(RonTypes.COLON)
            .spaceIf(true)
            .before(RonTypes.COMMA)
            .spaceIf(false)
            .after(RonTypes.COMMA)
            .spaceIf(true)
            .withinPair(RonTypes.LBRACK, RonTypes.RBRACK)
            .spaceIf(false)
            .withinPair(RonTypes.LBRACE, RonTypes.RBRACE)
            .spaceIf(false)
            .withinPair(RonTypes.LPAREN, RonTypes.RPAREN)
            .spaceIf(false)
    }
}

/**
 * Block that represents "do not touch this fragment" — used when the RON code is injected into
 * another language (Markdown fenced code blocks etc).
 */
private class NoopBlock(node: ASTNode) : AbstractBlock(node, null, null) {
    override fun buildChildren(): List<Block> = emptyList()

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = null

    override fun isLeaf(): Boolean = true

    override fun getIndent(): Indent = Indent.getAbsoluteNoneIndent()
}
