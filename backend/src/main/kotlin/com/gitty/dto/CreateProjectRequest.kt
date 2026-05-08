package com.gitty.dto

data class CreateProjectRequest(
    val name: String,
    val imageUrl: String? = null,
    val description: String? = null,
    val repoUrl: String? = null,
    val documentation: String? = null
)