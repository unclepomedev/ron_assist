# ron_assist

[![Version](https://img.shields.io/jetbrains/plugin/v/31724-ron-assist)](https://plugins.jetbrains.com/plugin/31724-ron-assist)


<!-- Plugin description -->
RON (Rust Object Notation) assist plugin for JetBrains IDEs (RustRover, IntelliJ IDEA, PyCharm, etc.)

## Features

### Editing
* Brace matcher (with Enter handler, smart backspace, surround with)
* Quote handler
* Commenter (line and block comments)
* Smart Enter processor (`Cmd+Shift+Enter` / `Ctrl+Shift+Enter`)
* Live templates
* Schema-based top-level field name completion for self-described RON files
* Create File from Template

### Highlighting & display
* Syntax highlighting
* Semantic highlighting (distinguishes struct names from field names)
* Color settings page
* Folding (PSI-based and `// region` markers, with per-type collapse settings)
* Structure view
* Breadcrumbs

### Code quality
* Inspections (duplicate map keys, duplicate struct fields)
* Spell checker

### Formatting
* Formatter (indent and spacing rules, optional trailing comma on multiline collections)
* Code style settings page

source code, quick usage and issue tracker: https://github.com/unclepomedev/ron_assist

<!-- Plugin description end -->

## Quick Usage

Usage and shortcuts conform to standard JetBrains IDE behavior. A few items
that benefit from explicit pointers:

- **New RON File**: `File → New → RON File`, or right-click in the Project
  view → `New → RON File`.

- **Smart Enter** (`Cmd+Shift+Enter` / `Ctrl+Shift+Enter`): completes the
  current entry with a trailing comma and moves to the next line, regardless
  of cursor position within the entry.

- **Folding**: PSI nodes fold automatically. Use `// region <name>` and
  `// endregion` to create custom collapsible sections.

- **Live Templates**: type a prefix and `Tab` to expand. Try `st` (named
  struct), `mp` (map), `lst` (list), `kv` (map entry), `field` (struct entry).
  Press `Cmd+J` / `Ctrl+J` to see all available templates in context.

- **Inspections**: duplicate map keys and struct fields are flagged with
  warnings. Standard and raw strings (`"foo"` and `r"foo"`) count as
  equivalent for duplicate detection. Configure under
  `Settings → Editor → Inspections → RON`.

- **Trailing Comma**: an opt-in formatter option to append trailing commas
  to multiline lists, maps, structs, and tuples. Configure under
  `Settings → Editor → Code Style → RON → Wrapping and Braces`.

- **Self-described RON files**: add `#![type = "my_crate::config::Config"]` at
  the top of a file to enable field name completion inside the top-level
  struct. Place a schema describing the type at
  `<schema dir>/my_crate/config/Config.schema.ron` (see [schema format](docs/schema.md#schema-format)).

  Schema directory: `$RON_SCHEMA_DIR` if set, otherwise the OS-standard data
  directory (see [details](docs/schema.md#schema-directory-resolution)).

## LICENSE

Apache-2.0 or MIT
