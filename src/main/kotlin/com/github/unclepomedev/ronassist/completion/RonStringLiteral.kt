package com.github.unclepomedev.ronassist.completion

object RonStringLiteral {
    /**
     * Unescapes standard and raw RON string literals into their decoded string values. Returns null
     * if the literal syntax is invalid, has invalid escape sequences, or is unclosed.
     */
    fun unescape(text: String?): String? {
        if (text == null) return null
        if (text.startsWith("r")) {
            val quote = text.indexOf('"')
            if (quote < 1) return null
            val hashes = text.substring(1, quote)
            if (hashes.any { it != '#' }) return null
            val suffix = "\"$hashes"
            if (text.length < quote + 1 + suffix.length || !text.endsWith(suffix)) return null
            return text.substring(quote + 1, text.length - suffix.length)
        }
        if (text.length < 2 || text.first() != '"' || text.last() != '"') return null
        val result = StringBuilder()
        var i = 1
        val end = text.lastIndex
        while (i < end) {
            val c = text[i++]
            if (c == '"' || c == '\n' || c == '\r') return null
            if (c != '\\') {
                result.append(c)
                continue
            }
            if (i >= end) return null
            when (val escaped = text[i++]) {
                '\\',
                '"',
                '\'' -> result.append(escaped)
                'n' -> result.append('\n')
                'r' -> result.append('\r')
                't' -> result.append('\t')
                '0' -> result.append('\u0000')
                'u' -> {
                    if (i >= end || text[i++] != '{') return null
                    val close = text.indexOf('}', i)
                    if (close !in i..<end) return null
                    val hex = text.substring(i, close)
                    if (!Regex("[0-9a-fA-F]{1,6}").matches(hex)) return null
                    val code = hex.toInt(16)
                    if (!Character.isValidCodePoint(code) || code in 0xD800..0xDFFF) return null
                    result.appendCodePoint(code)
                    i = close + 1
                }
                else -> return null
            }
        }
        return result.toString()
    }
}
