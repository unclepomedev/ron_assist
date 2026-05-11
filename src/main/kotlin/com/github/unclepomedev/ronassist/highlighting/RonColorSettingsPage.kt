package com.github.unclepomedev.ronassist.highlighting

import com.github.unclepomedev.ronassist.icons.RonIcons
import com.github.unclepomedev.ronassist.lang.RonLanguage
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class RonColorSettingsPage : ColorSettingsPage {

    private val descriptors = arrayOf(
        AttributesDescriptor("Keyword", RonSyntaxHighlighter.KEYWORD),
        AttributesDescriptor("Number", RonSyntaxHighlighter.NUMBER),
        AttributesDescriptor("String", RonSyntaxHighlighter.STRING),
        AttributesDescriptor("Line comment", RonSyntaxHighlighter.LINE_COMMENT),
        AttributesDescriptor("Block comment", RonSyntaxHighlighter.BLOCK_COMMENT),
        AttributesDescriptor("Identifier", RonSyntaxHighlighter.IDENTIFIER),
        AttributesDescriptor("Braces", RonSyntaxHighlighter.BRACES),
        AttributesDescriptor("Brackets", RonSyntaxHighlighter.BRACKETS),
        AttributesDescriptor("Parentheses", RonSyntaxHighlighter.PARENTHESES),
        AttributesDescriptor("Comma", RonSyntaxHighlighter.COMMA),
        AttributesDescriptor("Bad character", RonSyntaxHighlighter.BAD_CHARACTER)
    )

    override fun getIcon(): Icon = RonIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = RonSyntaxHighlighter()

    override fun getDemoText(): String = """
        // Example RON file
        /* 
           Block comment
        */
        Scene(
            materials: {
                "metal": (
                    reflectivity: 1.0,
                    roughness: 0.2,
                ),
                "plastic": (
                    reflectivity: 0.5,
                    roughness: 0.8,
                ),
            },
            entities: [
                (
                    name: "hero",
                    visible: true,
                    health: Some(100),
                    data: r#"Raw string example"#
                ),
                (
                    name: "monster",
                    visible: false,
                    health: None,
                ),
            ]
        )
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = descriptors

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = RonLanguage.INSTANCE.displayName
}
