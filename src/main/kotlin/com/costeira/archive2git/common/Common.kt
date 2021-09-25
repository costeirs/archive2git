package com.costeira.archive2git.common

import java.io.File
import java.util.jar.Manifest

const val DEFAULT_CONFIG_FILE_NAME = "archive2git.json"

fun firstNonBlank(vararg items: String?, default: String): String {
    return items.firstOrNull { !it.isNullOrBlank() } ?: default
}

fun getAppVersion(): String {
    val stream = object {}.javaClass.getResourceAsStream("/META-INF/MANIFEST.MF")
    val manifest = Manifest(stream)
    val attr = manifest.mainAttributes

    return attr.getValue("Implementation-Version") ?: "unknown"
}

fun getPathAndCanonical(file: File): String {
    val resolvedPathMessage =
        if (file.path != file.canonicalPath) " (resolved to ${file.canonicalPath})" else ""
    return "\"${file.path}\"" + resolvedPathMessage
}
