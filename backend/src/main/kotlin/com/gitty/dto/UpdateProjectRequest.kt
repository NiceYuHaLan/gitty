package com.gitty.dto

data class UpdateProjectRequest(
    val name: String? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val repoUrl: String? = null,
    val documentation: String? = null
)