# Schemas for Self-Described RON Files

RON Assist can provide field name completion inside the top-level struct of a RON file if a schema describing its Rust type is available.

## Schema Format

A schema file is itself a RON file (with extension `.schema.ron`) located at `<schema dir>/<type path>.schema.ron`, where `::` in the type path is replaced by `/` (e.g., `my_crate::config::Config` resolves to `my_crate/config/Config.schema.ron`).

A minimal schema file looks like:

```ron
Schema(
    kind: Struct(
        fields: [
            Field(name: "host", ty: String),
            Field(name: "port", ty: U16),
        ],
    ),
)
```

### Supported Schema Definition

- Root: `Schema(doc: Option<String>, kind: Struct)` (`doc` is optional)
- Kind: `Struct(fields: List<Field>)`
- Field: `Field(name: String, ty: Type, doc: Option<String>, optional: bool, flattened: bool)`
  - `name`: Field name identifier (required)
  - `ty`: Field type (required, e.g., primitive like `String`, `U16`, `Bool`, etc., or composite like `Option(...)`, `List(...)`, `Struct(...)`)
  - `doc`, `optional`, `flattened`: Optional metadata (fields marked `flattened: true` are excluded from top-level suggestions)

## Schema Directory Resolution

The schema directory is selected once per lookup (there is no chained search fallback):

1. **`$RON_SCHEMA_DIR`**: If this environment variable is set and non-empty, only that directory is used, even if the schema file is missing or the path is invalid.
2. **OS Default**: If `RON_SCHEMA_DIR` is not set:
   - **Linux / BSD**: `$XDG_DATA_HOME/ron-schemas` (if `$XDG_DATA_HOME` is unset, empty, or relative, falls back to `~/.local/share/ron-schemas`)
   - **macOS**: `~/Library/Application Support/ron-schemas`
   - **Windows**: `%APPDATA%/ron-schemas` (if `%APPDATA%` is unavailable, schema completion is disabled)

## Completion Behavior & Limitations

- **Top-level struct fields only**: Suggestions are provided only for field names inside the root struct. Nested structs, enums, tuples, and collection elements are not expanded.
- **Prefix matching & deduplication**: Suggestions are filtered by typed prefix, and fields already defined in the struct are excluded.
- **Graceful fallback**: If the schema is missing, unreadable, malformed, or exceeds 1 MiB, other completion behavior (such as live templates) is unaffected.
- **Hot-reloading**: Schema files are re-read on completion, so changes take effect immediately without restarting the IDE.
