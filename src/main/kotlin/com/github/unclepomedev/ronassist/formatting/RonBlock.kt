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
    private val spacingBuilder: SpacingBuilder,
) : AbstractBlock(node, wrap, alignment) {

    /**
     * Builds child formatting blocks for the current AST node, filtering out whitespaces and empty
     * nodes.
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
     * Calculates the block's indentation, applying normal indents inside maps, lists, and structs
     * while keeping enclosing brackets and identifiers unindented.
     */
    override fun getIndent(): Indent? {
        val parentType = myNode.treeParent?.elementType ?: return Indent.getNoneIndent()
        if (parentType !in INDENT_CONTAINERS) return Indent.getNoneIndent()
        if (myNode.elementType in NO_INDENT_TOKENS) return Indent.getNoneIndent()
        return Indent.getNormalIndent()
    }

    /**
     * Defines indentation attributes for newly inserted child blocks, ensuring standard indents
     * when adding items inside maps, lists, or structs.
     */
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        return if (myNode.elementType in INDENT_CONTAINERS) {
            ChildAttributes(Indent.getNormalIndent(), null)
        } else {
            ChildAttributes(Indent.getNoneIndent(), null)
        }
    }

    /**
     * Determines the required spacing between two child blocks based solely on the predefined
     * spacing rules. Empty bracket pairs are forced to zero spacing; otherwise the spacing rules
     * are consulted. Returns null if no rule applies.
     */
    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        if (isEmptyBracketPair(child1, child2)) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    /**
     * Returns true if the block has no child nodes, marking it as a leaf in the formatting tree.
     */
    override fun isLeaf(): Boolean = myNode.firstChildNode == null

    private fun isEmptyBracketPair(child1: Block?, child2: Block?): Boolean {
        if (child1 == null || child2 == null) return false
        val leftType = (child1 as? RonBlock)?.node?.elementType ?: return false
        val rightType = (child2 as? RonBlock)?.node?.elementType ?: return false

        return when (leftType) {
            RonTypes.LBRACK -> rightType == RonTypes.RBRACK
            RonTypes.LBRACE -> rightType == RonTypes.RBRACE
            RonTypes.LPAREN -> rightType == RonTypes.RPAREN
            else -> false
        }
    }

    companion object {
        /** AST element types whose direct children should be indented. */
        private val INDENT_CONTAINERS =
            setOf(
                RonTypes.MAP,
                RonTypes.LIST,
                RonTypes.STRUCT_OR_TUPLE,
                RonTypes.OPTION,
            )

        /** Token types that should not be indented even when inside an indent container. */
        private val NO_INDENT_TOKENS =
            setOf(
                RonTypes.LBRACE,
                RonTypes.RBRACE,
                RonTypes.LBRACK,
                RonTypes.RBRACK,
                RonTypes.LPAREN,
                RonTypes.RPAREN,
                RonTypes.IDENTIFIER,
                RonTypes.SOME,
                RonTypes.NONE,
            )
    }
}
