package common

fun firstNonEmpty(vararg items: String?, default: String): String {
    return items.first { !it.isNullOrEmpty() } ?: default
}