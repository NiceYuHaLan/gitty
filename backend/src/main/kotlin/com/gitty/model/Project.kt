package com.gitty.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,
    val imageUrl: String,
    val description: String? = null,

    @Column(name = "repo_url")
    val repoUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    val documentation: String? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    val createdAt: LocalDateTime = LocalDateTime.now()
)