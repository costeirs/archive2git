package com.costeira.archive2git.common

fun firstNonBlank(vararg items: String?, default: String): String {
    return items.firstOrNull { !it.isNullOrBlank() } ?: default
}

const val defaultConfigFileName = "archive2git.json"
