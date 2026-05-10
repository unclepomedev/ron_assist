package com.github.unclepomedev.ronassist.parser

import com.github.unclepomedev.ronassist.lexer.RonLexerAdapter
import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class RonParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = RonLexerAdapter()

    override fun createParser(project: Project?): PsiParser = RonParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = TokenSet.create(RonTypes.LINE_COMMENT, RonTypes.BLOCK_COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.create(RonTypes.STRING, RonTypes.RAW_STRING)

    override fun createElement(node: ASTNode?): PsiElement = RonTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = RonFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements =
        ParserDefinition.SpaceRequirements.MAY

    companion object {
        val FILE = IFileElementType(com.github.unclepomedev.ronassist.lang.RonLanguage.INSTANCE)
    }
}
