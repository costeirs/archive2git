package com.costeira.archive2git.models

import com.costeira.archive2git.serializers.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ReleasesFolder(
    val path: String,
    val title: String,
    @Serializable(with = DateSerializer::class)
    val at: LocalDateTime? = null,
    val committer: String? = null
)
