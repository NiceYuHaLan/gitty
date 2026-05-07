package com.gitty.dto

data class CreateProjectRequest(
    val name: String,
    val description: String? = null,
    val repoUrl: String? = null
)