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

    /**
     * Builds child formatting blocks for the current AST node, filtering out whitespaces and empty nodes.
     */
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

    /**
     * Calculates the block's indentation, applying normal indents inside maps, lists,
     * and structs while keeping enclosing brackets and identifiers unindented.
     */
    override fun getIndent(): Indent? {
        val parentType = myNode.treeParent?.elementType
        val type = myNode.elementType

        if (parentType == RonTypes.MAP || parentType == RonTypes.LIST || parentType == RonTypes.STRUCT_OR_TUPLE) {
            if (type == RonTypes.LBRACE || type == RonTypes.RBRACE ||
                type == RonTypes.LBRACK || type == RonTypes.RBRACK ||
                type == RonTypes.LPAREN || type == RonTypes.RPAREN ||
                type == RonTypes.IDENTIFIER
            ) {
                return Indent.getNoneIndent()
            }
            return Indent.getNormalIndent()
        }
        return Indent.getNoneIndent()
    }

    /**
     * Defines indentation attributes for newly inserted child blocks, ensuring standard
     * indents when adding items inside maps, lists, or structs.
     */
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val type = myNode.elementType
        if (type == RonTypes.MAP || type == RonTypes.LIST || type == RonTypes.STRUCT_OR_TUPLE) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }
        return ChildAttributes(Indent.getNoneIndent(), null)
    }

    /**
     * Determines spacing between child blocks using predefined rules, falling back
     * to a default line-break-preserving spacing if no specific rule applies.
     */
    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        val spacing = spacingBuilder.getSpacing(this, child1, child2)
        if (spacing != null) return spacing
        return Spacing.createSpacing(0, Int.MAX_VALUE, 0, true, 1)
    }

    /**
     * Returns true if the block has no child nodes, marking it as a leaf in the formatting tree.
     */
    override fun isLeaf(): Boolean = myNode.firstChildNode == null
}
