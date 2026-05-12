# ron_assist

<!-- Plugin description -->
RON (Redshift Node Operator) assist plugin for JetBrains IDEs (RustRover, IntelliJ IDEA, PyCharm, etc.)

## Features

### Editing
* Brace matcher (with Enter handler, smart backspace, surround with)
* Quote handler
* Commenter (line and block comments)
* Smart Enter processor (`Cmd+Shift+Enter` / `Ctrl+Shift+Enter`)
* Live templates
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
* Formatter (indent and spacing rules)
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

## LICENSE

Apache-2.0 or MIT
