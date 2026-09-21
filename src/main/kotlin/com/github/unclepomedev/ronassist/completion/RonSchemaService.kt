package com.github.unclepomedev.ronassist.completion

import com.github.unclepomedev.ronassist.lang.RON_FILE_TYPE
import com.github.unclepomedev.ronassist.psi.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

private data class RonSchema(
    val doc: String?,
    val kind: RonSchemaKind,
)

private sealed interface RonSchemaKind {
    data class Struct(val fields: List<RonSchemaField>) : RonSchemaKind
}

private data class RonSchemaField(
    val name: String,
    val ty: RonValue,
    val doc: String?,
    val optional: Boolean,
    val flattened: Boolean,
)

@Service(Service.Level.PROJECT)
class RonSchemaService
private constructor(private val project: Project, private val directoryProvider: () -> Path?) {
    constructor(project: Project) : this(project, RonSchemaDirectoryResolver::defaultDirectory)

    internal fun fields(typePath: String, directory: Path? = directoryProvider()): List<String>? {
        if (!TYPE_PATH.matches(typePath) || directory == null) return null
        val relative = typePath.replace("::", "/") + ".schema.ron"
        val root =
            try {
                directory.toRealPath()
            } catch (_: IOException) {
                return null
            } catch (_: SecurityException) {
                return null
            }

        val file =
            try {
                root.resolve(relative).toRealPath()
            } catch (_: IOException) {
                return null
            } catch (_: SecurityException) {
                return null
            }

        if (!file.startsWith(root) || !Files.isRegularFile(file)) return null

        val text = readSchemaFile(file) ?: return null
        return parseFields(text)
    }

    private fun readSchemaFile(file: Path): String? =
        try {
            val bytes = Files.newInputStream(file).use { it.readNBytes(MAX_SCHEMA_BYTES + 1) }
            if (bytes.size > MAX_SCHEMA_BYTES) null
            else {
                StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString()
            }
        } catch (_: IOException) {
            null
        } catch (_: SecurityException) {
            null
        }

    internal fun parseFields(text: String): List<String>? {
        val file =
            PsiFileFactory.getInstance(project)
                .createFileFromText("schema.ron", RON_FILE_TYPE, text)
        if (PsiTreeUtil.findChildOfType(file, PsiErrorElement::class.java) != null) return null
        if (!hasValidDelimiters(file)) return null
        if (file.children.any { it is RonAttribute }) return null

        val schema = parseSchema(file) ?: return null
        return extractFieldNames(schema)
    }

    private fun parseSchema(file: PsiElement): RonSchema? {
        val value = file.children.filterIsInstance<RonValue>().singleOrNull() ?: return null
        val schemaEntries = members(value, "Schema") ?: return null
        if (!validateAllowedKeys(schemaEntries, setOf("kind", "doc"))) return null

        val docValue = schemaEntries["doc"]
        val docString =
            parseDoc(docValue)
                ?: if (docValue != null && docValue.text != "None") return null else null

        val kindEntries = members(schemaEntries["kind"], "Struct") ?: return null
        if (
            !validateAllowedKeys(kindEntries, setOf("fields")) ||
                kindEntries.keys != setOf("fields")
        )
            return null

        val fieldsList = kindEntries["fields"]?.list ?: return null
        val fieldItems = mutableListOf<RonSchemaField>()

        for (field in fieldsList.valueList) {
            ProgressManager.checkCanceled()
            val entries = members(field, "Field") ?: return null
            if (!validateAllowedKeys(entries, setOf("name", "ty", "doc", "optional", "flattened")))
                return null

            val name = RonStringLiteral.unescape(entries["name"]?.stringVal?.text) ?: return null
            val ty = entries["ty"] ?: return null
            if (!validType(ty)) return null

            val fieldDocValue = entries["doc"]
            val fieldDoc =
                parseDoc(fieldDocValue)
                    ?: if (fieldDocValue != null && fieldDocValue.text != "None") return null
                    else null

            for (flag in listOf("optional", "flattened")) {
                if (entries[flag] != null && entries[flag]?.boolean == null) return null
            }
            val optional = entries["optional"]?.boolean?.text == "true"
            val flattened = entries["flattened"]?.boolean?.text == "true"

            fieldItems.add(RonSchemaField(name, ty, fieldDoc, optional, flattened))
        }

        return RonSchema(docString, RonSchemaKind.Struct(fieldItems))
    }

    private fun extractFieldNames(schema: RonSchema): List<String>? {
        val result = mutableListOf<String>()
        val names = mutableSetOf<String>()
        val fields =
            when (val kind = schema.kind) {
                is RonSchemaKind.Struct -> kind.fields
            }

        for ((name, _, _, _, flattened) in fields) {
            ProgressManager.checkCanceled()
            if (!IDENTIFIER.matches(name) || name in KEYWORDS || !names.add(name)) return null
            if (!flattened) {
                result.add(name)
            }
        }
        return result
    }

    private fun validateAllowedKeys(
        entries: Map<String, RonValue>,
        allowedKeys: Set<String>,
    ): Boolean = entries.keys.all { it in allowedKeys }

    /**
     * Checks whether delimiters (commas and closing brackets) are placed correctly in composite
     * structures, and string literals are well-formed.
     *
     * This verification is necessary because parser error recovery in incomplete user input or
     * corrupted schemas might produce valid-looking AST nodes while skipping missing commas or
     * unbalanced delimiters.
     */
    private fun hasValidDelimiters(file: PsiElement): Boolean {
        val pending = ArrayDeque<PsiElement>()
        pending.add(file)
        while (pending.isNotEmpty()) {
            ProgressManager.checkCanceled()
            val element = pending.removeLast()
            if (element is RonStringVal && RonStringLiteral.unescape(element.text) == null)
                return false
            val children =
                generateSequence(element.firstChild) { it.nextSibling }
                    .filter { it !is PsiWhiteSpace && it !is PsiComment }
                    .toList()
            if (element is RonStructOrTuple || element is RonList || element is RonMap) {
                for ((index, child) in children.withIndex()) {
                    if (child !is RonValue && child !is RonStructEntry && child !is RonMapEntry)
                        continue
                    val next = children.getOrNull(index + 1)?.node?.elementType
                    if (
                        next !in
                            setOf(RonTypes.COMMA, RonTypes.RPAREN, RonTypes.RBRACK, RonTypes.RBRACE)
                    )
                        return false
                }
            }
            pending.addAll(children)
        }
        return true
    }

    private fun validType(value: RonValue?): Boolean {
        if (value == null) return false
        if (value.identifier != null) return value.identifier!!.text in PRIMITIVES
        val struct = value.structOrTuple ?: return false
        return when (struct.identifier?.text) {
            "Option",
            "List",
            "Tuple",
            "TypeRef" -> struct.structEntryList.isEmpty() && struct.valueList.size == 1
            "Struct",
            "Enum",
            "Map" -> struct.valueList.isEmpty() && struct.structEntryList.isNotEmpty()
            else -> false
        }
    }

    private fun members(value: RonValue?, name: String): Map<String, RonValue>? {
        val struct = value?.structOrTuple ?: return null
        if (struct.identifier?.text != name || struct.valueList.isNotEmpty()) return null
        val result = linkedMapOf<String, RonValue>()
        for (entry in struct.structEntryList) {
            val fieldValue = entry.value ?: return null
            if (result.put(entry.identifier.text, fieldValue) != null) return null
        }
        return result
    }

    private fun parseDoc(value: RonValue?): String? =
        when {
            value == null -> null
            value.text == "None" -> null
            else -> value.option?.value?.stringVal?.text?.let(RonStringLiteral::unescape)
        }

    companion object {
        internal fun forDirectory(project: Project, directory: Path): RonSchemaService =
            RonSchemaService(project) { directory }

        private const val MAX_SCHEMA_BYTES = 1024 * 1024
        private val TYPE_PATH = Regex("[a-zA-Z_][a-zA-Z0-9_]*(::[a-zA-Z_][a-zA-Z0-9_]*)*")
        private val IDENTIFIER = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
        private val KEYWORDS = setOf("true", "false", "Some", "None")
        private val PRIMITIVES =
            setOf(
                "Bool",
                "I8",
                "I16",
                "I32",
                "I64",
                "I128",
                "U8",
                "U16",
                "U32",
                "U64",
                "U128",
                "F32",
                "F64",
                "Char",
                "String",
                "Unit",
            )
    }
}
