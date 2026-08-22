<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# ron_assist Changelog

## [Unreleased]

## [0.1.11] - 2026-08-22

* Added an opt-in "Add trailing comma" formatter option (Settings | Editor | Code Style | RON | Wrapping and Braces) that appends trailing commas to multiline lists, maps, structs and tuples.

## [0.1.10] - 2026-07-02

* Fixed integer and float literals with `_` digit separators being flagged as syntax errors.
    * Malformed placements (consecutive, trailing, or right after a base prefix) are not merged into the numeric token.

## [0.1.9] - 2026-06-20

* Improved "New RON File" action availability and menu organization.

## [0.1.8] - 2026-06-17

* Compact formatting for empty bracket pairs.

## [0.1.7] - 2026-06-09

* Enabled Enter-between-braces for all RON parenthesis pairs.
* Smart Enter now completes list and tuple elements, inserting the missing comma and newline at the element level.

## [0.1.6] - 2026-05-19

* Disabled formatter for injected RON fragments.

## [0.1.5] - 2026-05-18

* Removed LSP4IJ plugin dependency.

## [0.1.4] - 2026-05-15

* Fix missing comma error detection in collections.

## [0.1.3] - 2026-05-14

* Improved formatter handling of incomplete struct and map entries without colon separators.

## [0.1.2] - 2026-05-13

* Fix Smart Enter to correctly append missing commas and newlines.

## [0.1.1] - 2026-05-13

* Description Typo fix
* Improved quote handling for string and character literals.
* Icon fix

## [0.1.0] - 2026-05-12

### Added

* Brace matcher (with Enter handler, smart backspace, surround with)
* Quote handler
* Commenter (line and block comments)
* Smart Enter processor (`Cmd+Shift+Enter` / `Ctrl+Shift+Enter`)
* Live templates
* Create File from Template
* Syntax highlighting
* Semantic highlighting (distinguishes struct names from field names)
* Color settings page
* Folding (PSI-based and `// region` markers, with per-type collapse settings)
* Structure view
* Breadcrumbs
* Inspections (duplicate map keys, duplicate struct fields)
* Spell checker
* Formatter (indent and spacing rules)
* Code style settings page
