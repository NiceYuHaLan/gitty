package com.gitty.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var name: String,
    var imageUrl: String? = null,
    var description: String? = null,

    @Column(name = "repo_url")
    var repoUrl: String? = null,

    @Column(name = "repo_full_name")
    var repoFullName: String? = null,

    @Column(columnDefinition = "TEXT")
    var documentation: String? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    var createdAt: LocalDateTime = LocalDateTime.now()
)