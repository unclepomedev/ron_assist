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
        AttributesDescriptor("String//Standard", RonSyntaxHighlighter.STRING),
        AttributesDescriptor("String//Raw", RonSyntaxHighlighter.RAW_STRING),
        AttributesDescriptor("String//Char", RonSyntaxHighlighter.CHAR),
        AttributesDescriptor("Comment//Line comment", RonSyntaxHighlighter.LINE_COMMENT),
        AttributesDescriptor("Comment//Block comment", RonSyntaxHighlighter.BLOCK_COMMENT),
        AttributesDescriptor("Identifier//Other", RonSyntaxHighlighter.IDENTIFIER),
        AttributesDescriptor("Identifier//Struct name", RonSyntaxHighlighter.STRUCT_NAME),
        AttributesDescriptor("Braces and operators//Braces", RonSyntaxHighlighter.BRACES),
        AttributesDescriptor("Braces and operators//Brackets", RonSyntaxHighlighter.BRACKETS),
        AttributesDescriptor("Braces and operators//Parentheses", RonSyntaxHighlighter.PARENTHESES),
        AttributesDescriptor("Braces and operators//Comma", RonSyntaxHighlighter.COMMA),
        AttributesDescriptor("Bad character", RonSyntaxHighlighter.BAD_CHARACTER),
    )

    override fun getIcon(): Icon = RonIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = RonSyntaxHighlighter()

    override fun getDemoText(): String = """
        // Example RON file
        /*
           Block comment
        */
        <structName>Scene</structName>(
            materials: {
                "metal": (
                    reflectivity: 1.0,
                    roughness: 0.2,
                    priority: 100u32,
                ),
                "plastic": (
                    reflectivity: 0.5f64,
                    roughness: 0.8,
                ),
            },
            entities: [
                (
                    name: "hero",
                    visible: true,
                    health: Some(100),
                    grade: 'A',
                    data: r#"Raw string example"#,
                ),
                (
                    name: "monster",
                    visible: false,
                    health: None,
                ),
            ],
        )
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "structName" to RonSyntaxHighlighter.STRUCT_NAME,
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = descriptors

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = RonLanguage.INSTANCE.displayName
}
