package models

import kotlinx.serialization.Serializable
import serializers.DateSerializer
import java.time.LocalDateTime

@Serializable
data class ReleasesFolder(
    val path: String,
    val title: String,
    @Serializable(with = DateSerializer::class)
    val at: LocalDateTime? = null,
    val committer: String? = null
)
