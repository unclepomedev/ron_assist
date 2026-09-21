package com.github.unclepomedev.ronassist.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Files
import java.nio.file.Path

class RonSchemaServiceTest : BasePlatformTestCase() {
    private val service
        get() = RonSchemaService(project)

    private lateinit var directory: Path

    override fun setUp() {
        super.setUp()
        val base = Path.of("build", "schema-test-tmp").toAbsolutePath()
        Files.createDirectories(base)
        directory = Files.createTempDirectory(base, "service-")
    }

    override fun tearDown() {
        try {
            Files.walk(directory).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        } finally {
            super.tearDown()
        }
    }

    fun testGeneratedShape() {
        assertEquals(
            listOf("host", "hostname", "port"),
            service.parseFields(RonSchemaCompletionTest.SCHEMA),
        )
    }

    fun testEmptyFields() {
        assertEquals(emptyList<String>(), service.parseFields("Schema(kind: Struct(fields: []))"))
    }

    fun testMetadataAndNestedType() {
        assertEquals(
            listOf("nested"),
            service.parseFields(
                """Schema(doc: Some("configuration"), kind: Struct(fields: [
            Field(name: "nested", ty: Struct(fields: [Field(name: "inner", ty: String)]), doc: None, optional: true),
            Field(name: "flattened", ty: String, flattened: true),
        ]))"""
            ),
        )
    }

    fun testInvalidSchemas() {
        for (text in
            listOf(
                "",
                "{}",
                "Schema()",
                "Schema(kind: String)",
                "Schema(kind: Struct(fields: 1))",
                "Schema(kind: Struct(fields: [Field(name: \"host\")]))",
                "Schema(kind: Struct(fields: [Field(name: 1, ty: String)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\" ty: String)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: 1)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: Unknown)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: String) Field(name: \"port\", ty: U16)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: String, optional: 1)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: String, doc: 1)]))",
                "Schema(kind: Struct(fields: [Field(name: \"ho\\qst\", ty: String)]))",
                "Schema(kind: Struct(fields: [Field(name: \"host\", ty: String), Field(name: \"host\", ty: String)]))",
                "Schema(kind: Struct(fields: []), kind: Struct(fields: []))",
                "Schema(kind: Struct(fields: [])) trailing",
                "Schema(kind: Struct(fields: [",
            )) assertNull(text, service.parseFields(text))
    }

    fun testStrings() {
        assertEquals("host", RonStringLiteral.unescape("\"ho\\u{73}t\""))
        assertEquals("host", RonStringLiteral.unescape("r##\"host\"##"))
        for (text in
            listOf(
                "\"unterminated",
                "\"bad\\q\"",
                "\"\\u{D800}\"",
                "r#\"host\"",
                "\"\\u{110000}\"",
            )) {
            assertNull(text, RonStringLiteral.unescape(text))
        }
    }

    fun testDirectorySelection() {
        val home = directory
        val data = home.resolve("data")
        val override = home.resolve("override")
        assertEquals(
            home.resolve(".local/share/ron-schemas"),
            RonSchemaDirectoryResolver.resolveDirectory("unix", home.toString(), emptyMap()),
        )
        assertEquals(
            home.resolve("Library/Application Support/ron-schemas"),
            RonSchemaDirectoryResolver.resolveDirectory("mac", home.toString(), emptyMap()),
        )
        assertEquals(
            data.resolve("ron-schemas"),
            RonSchemaDirectoryResolver.resolveDirectory(
                "windows",
                home.toString(),
                mapOf("APPDATA" to data.toString()),
            ),
        )
        assertNull(
            RonSchemaDirectoryResolver.resolveDirectory("windows", home.toString(), emptyMap())
        )
        assertEquals(
            data.resolve("ron-schemas"),
            RonSchemaDirectoryResolver.resolveDirectory(
                "unix",
                home.toString(),
                mapOf("XDG_DATA_HOME" to data.toString()),
            ),
        )
        for (xdg in listOf("", "relative", "\u0000")) {
            assertEquals(
                home.resolve(".local/share/ron-schemas"),
                RonSchemaDirectoryResolver.resolveDirectory(
                    "unix",
                    home.toString(),
                    mapOf("XDG_DATA_HOME" to xdg),
                ),
            )
        }
        for (os in listOf("unix", "mac", "windows")) {
            assertEquals(
                override,
                RonSchemaDirectoryResolver.resolveDirectory(
                    os,
                    home.toString(),
                    mapOf(
                        "RON_SCHEMA_DIR" to override.toString(),
                        "XDG_DATA_HOME" to data.toString(),
                    ),
                ),
            )
            for (invalid in listOf("", "\u0000")) {
                assertNull(
                    RonSchemaDirectoryResolver.resolveDirectory(
                        os,
                        home.toString(),
                        mapOf("RON_SCHEMA_DIR" to invalid),
                    )
                )
            }
        }
    }

    fun testFilesAndInvalidPaths() {
        val root = directory
        val schema = root.resolve("example/Config.schema.ron")
        Files.createDirectories(schema.parent)
        Files.writeString(schema, RonSchemaCompletionTest.SCHEMA)
        assertEquals(listOf("host", "hostname", "port"), service.fields("example::Config", root))
        for (type in
            listOf(
                "",
                "../example::Config",
                "example::..::Config",
                "/example::Config",
                "example::Config/",
                "example::Missing",
            )) {
            assertNull(type, service.fields(type, root))
        }
        assertNull(service.fields("example::Config", root.resolve("missing")))
        Files.write(schema, byteArrayOf(0xC3.toByte(), 0x28))
        assertNull(service.fields("example::Config", root))
        Files.writeString(schema, " ".repeat(1024 * 1024 + 1))
        assertNull(service.fields("example::Config", root))
    }

    fun testOverrideDoesNotFallBack() {
        val fallback = directory.resolve(".local/share/ron-schemas/example/Config.schema.ron")
        Files.createDirectories(fallback.parent)
        Files.writeString(fallback, RonSchemaCompletionTest.SCHEMA)
        val selected =
            RonSchemaDirectoryResolver.resolveDirectory(
                "unix",
                directory.toString(),
                mapOf("RON_SCHEMA_DIR" to directory.resolve("absent").toString()),
            )
        assertNull(service.fields("example::Config", selected))
        assertEquals(
            listOf("host", "hostname", "port"),
            service.fields(
                "example::Config",
                RonSchemaDirectoryResolver.resolveDirectory(
                    "unix",
                    directory.toString(),
                    emptyMap(),
                ),
            ),
        )
    }
}
