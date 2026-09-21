package com.github.unclepomedev.ronassist.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.replaceService
import java.nio.file.Files
import java.nio.file.Path

class RonSchemaCompletionTest : BasePlatformTestCase() {
    private lateinit var directory: Path
    private lateinit var schema: Path

    override fun setUp() {
        super.setUp()
        val base = Path.of("build", "schema-test-tmp").toAbsolutePath()
        Files.createDirectories(base)
        directory = Files.createTempDirectory(base, "completion-")
        schema = directory.resolve("example/Config.schema.ron")
        Files.createDirectories(schema.parent)
        Files.writeString(schema, SCHEMA)
        project.replaceService(
            RonSchemaService::class.java,
            RonSchemaService.forDirectory(project, directory),
            testRootDisposable,
        )
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

    fun testEmptyStruct() = checkFields("(<caret>)", "host", "hostname", "port")

    fun testUnclosedStruct() = checkFields("(ho<caret>", "host", "hostname")

    fun testNamedStruct() = checkFields("Config(<caret>)", "host", "hostname", "port")

    fun testPrefix() = checkFields("(ho<caret>)", "host", "hostname")

    fun testNoMatch() = checkFields("(zz<caret>)")

    fun testExistingField() = checkFields("(host: 1, <caret>)", "hostname", "port")

    fun testEditingExistingName() = checkFields("(ho<caret>: 1, port: 2)", "host", "hostname")

    fun testValuePosition() = checkFields("(host: <caret>)")

    fun testNestedStruct() = checkFields("(nested: (<caret>))")

    fun testList() = checkFields("[<caret>]")

    fun testNestedList() = checkFields("(items: [<caret>])")

    fun testMap() = checkFields("{<caret>}")

    fun testTuple() = checkFields("(1, <caret>)")

    fun testComment() = checkFields("(// <caret>\n)")

    fun testString() = checkFields("(host: \"<caret>\")")

    fun testTypeMissing() = checkFields("(<caret>)", header = "")

    fun testTypeOnlyInComment() = checkFields("(<caret>)", header = "// $HEADER")

    fun testTypeOnlyInString() =
        checkFields("\"#![type = \\\"example::Config\\\"]<caret>\"", header = "")

    fun testWrongAttribute() = checkFields("(<caret>)", header = "#![other = \"example::Config\"]")

    fun testDuplicateType() = checkFields("(<caret>)", header = "$HEADER\n$HEADER")

    fun testMalformedAttribute() =
        checkFields("(<caret>)", header = "#![type = \"example::Config\"")

    fun testAttributePosition() = checkFields("#![type = \"exam<caret>\"]\n()", header = "")

    fun testTraversal() = checkFields("(<caret>)", header = "#![type = \"../example::Config\"]")

    fun testRawType() =
        checkFields(
            "(<caret>)",
            "host",
            "hostname",
            "port",
            header = "#![type = r#\"example::Config\"#]",
        )

    fun testEscapedType() =
        checkFields(
            "(<caret>)",
            "host",
            "hostname",
            "port",
            header = "#![type = \"example::Con\\u{66}ig\"]",
        )

    fun testLeadingComment() =
        checkFields("(<caret>)", "host", "hostname", "port", header = "// config\n$HEADER")

    fun testMissingSchema() {
        Files.delete(schema)
        checkFields("(<caret>)")
    }

    fun testBrokenSchema() {
        Files.writeString(schema, "Schema(kind: Struct(fields: [")
        checkFields("(<caret>)")
    }

    fun testEnumSchema() {
        Files.writeString(schema, "Schema(kind: Enum(variants: []))")
        checkFields("(<caret>)")
    }

    fun testSingleMatchInsertion() {
        myFixture.configureByText("config.ron", "$HEADER\n(por<caret>)")
        myFixture.completeBasic()
        myFixture.checkResult("$HEADER\n(port)")
    }

    fun testSchemaChangesAreVisible() {
        checkFields("(ho<caret>)", "host", "hostname")
        Files.writeString(
            schema,
            "Schema(kind: Struct(fields: [Field(name: \"hotel\", ty: String)]))",
        )
        checkFields("(ho<caret>)", "hotel")
    }

    private fun checkFields(body: String, vararg expected: String, header: String = HEADER) {
        myFixture.configureByText("config.ron", "$header\n$body")
        val completion = myFixture.completeBasic()
        if (completion == null && expected.size == 1) {
            val before = body.substringBefore("<caret>")
            val prefix = before.takeLastWhile { it.isLetterOrDigit() || it == '_' }
            myFixture.checkResult(
                "$header\n${before.dropLast(prefix.length)}${expected.single()}${body.substringAfter("<caret>")}"
            )
            return
        }
        val items = completion?.map { it.lookupString }.orEmpty()
        val schemaNames = setOf("host", "hostname", "port", "hotel")
        assertEquals(expected.toSet(), items.filter { it in schemaNames }.toSet())
    }

    companion object {
        private const val HEADER = "#![type = \"example::Config\"]"
        internal const val SCHEMA =
            """Schema(kind: Struct(fields: [
            Field(name: "host", ty: String),
            Field(name: "hostname", ty: String),
            Field(name: "port", ty: U16),
        ]))"""
    }
}
