package com.gitty.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "project")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "repo_url")
    val repoUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    val documentation: String? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    val createdAt: LocalDateTime = LocalDateTime.now()
)