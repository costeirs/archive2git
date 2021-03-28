package common

fun firstNonEmpty(vararg items: String?, default: String): String {
    return items.first { !it.isNullOrEmpty() } ?: default
}

const val defaultConfigFileName = "archive2git.json"