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
        AttributesDescriptor("Identifier//Field name", RonSyntaxHighlighter.FIELD_NAME),
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
            <fieldName>materials</fieldName>: {
                "metal": (
                    <fieldName>reflectivity</fieldName>: 1.0,
                    <fieldName>roughness</fieldName>: 0.2,
                    <fieldName>priority</fieldName>: 100u32,
                ),
                "plastic": (
                    <fieldName>reflectivity</fieldName>: 0.5f64,
                    <fieldName>roughness</fieldName>: 0.8,
                ),
            },
            <fieldName>entities</fieldName>: [
                (
                    <fieldName>name</fieldName>: "hero",
                    <fieldName>visible</fieldName>: true,
                    <fieldName>health</fieldName>: Some(100),
                    <fieldName>grade</fieldName>: 'A',
                    <fieldName>data</fieldName>: r#"Raw string example"#,
                ),
                (
                    <fieldName>name</fieldName>: "monster",
                    <fieldName>visible</fieldName>: false,
                    <fieldName>health</fieldName>: None,
                ),
            ],
        )
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "structName" to RonSyntaxHighlighter.STRUCT_NAME,
        "fieldName" to RonSyntaxHighlighter.FIELD_NAME,
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = descriptors

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = RonLanguage.INSTANCE.displayName
}
