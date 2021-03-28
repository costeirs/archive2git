package com.costeira.archive2git.models

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val releases: List<ReleasesFolder>,
    val committer: String? = "archive2git"
)
