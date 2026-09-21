package com.github.unclepomedev.ronassist.completion

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.EnvironmentUtil
import java.nio.file.InvalidPathException
import java.nio.file.Path

object RonSchemaDirectoryResolver {
    fun defaultDirectory(): Path? =
        resolveDirectory(
            when {
                SystemInfo.isWindows -> "windows"
                SystemInfo.isMac -> "mac"
                else -> "unix"
            },
            System.getProperty("user.home"),
            EnvironmentUtil.getEnvironmentMap(),
        )

    internal fun resolveDirectory(os: String, home: String?, env: Map<String, String>): Path? {
        fun path(value: String?): Path? =
            try {
                value?.takeIf { it.isNotBlank() }?.let(Path::of)
            } catch (_: InvalidPathException) {
                null
            }
        if (env.containsKey("RON_SCHEMA_DIR")) return path(env["RON_SCHEMA_DIR"])
        val base =
            when (os) {
                "windows" -> path(env["APPDATA"])
                "mac" -> path(home)?.resolve("Library/Application Support")
                else ->
                    path(env["XDG_DATA_HOME"])?.takeIf { it.isAbsolute }
                        ?: path(home)?.resolve(".local/share")
            }
        return base?.takeIf { it.isAbsolute }?.resolve("ron-schemas")
    }
}
