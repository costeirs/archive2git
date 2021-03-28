package com.costeira.archive2git.models

data class Settings(
    val releases: List<ReleasesFolder>,
    val committer: String? = "archive2git"
)
