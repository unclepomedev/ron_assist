package com.github.unclepomedev.ronassist.formatting

import com.github.unclepomedev.ronassist.psi.RonTypes
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock

class RonBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE && child.textRange.length > 0) {
                blocks.add(RonBlock(child, null, null, spacingBuilder))
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getIndent(): Indent? {
        val parentType = myNode.treeParent?.elementType
        val type = myNode.elementType

        if (parentType == RonTypes.MAP || parentType == RonTypes.LIST || parentType == RonTypes.STRUCT_OR_TUPLE) {
            if (type == RonTypes.LBRACE || type == RonTypes.RBRACE ||
                type == RonTypes.LBRACK || type == RonTypes.RBRACK ||
                type == RonTypes.LPAREN || type == RonTypes.RPAREN
            ) {
                return Indent.getNoneIndent()
            }
            return Indent.getNormalIndent()
        }
        return Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean = myNode.firstChildNode == null
}
