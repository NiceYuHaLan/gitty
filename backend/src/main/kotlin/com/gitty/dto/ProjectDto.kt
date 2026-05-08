package com.gitty.dto

data class ProjectDto(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val description: String?,
    val userId: Long,
    val repoUrl: String? = null,
    val documentation: String? = null,
    val createdAt: String? = null
)