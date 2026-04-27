package com.gitty.model

import jakarta.persistence.*

@Entity
@Table(name = "app_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = true)
    val password: String? = null,

    val email: String? = null,

    @Column(name = "github_id")
    val githubId: String? = null,

    @Column(name = "github_avatar")
    val githubAvatar: String? = null,

    @Column(name = "github_access_token")
    val githubAccessToken: String? = null,

    @Column(name = "provider")
    val provider: String? = null,

    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)