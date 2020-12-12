package models

import kotlinx.serialization.Serializable

@Serializable
data class ReleasesFolder(
    val path: String,
    val title: String,
    val message: String? = null
)
