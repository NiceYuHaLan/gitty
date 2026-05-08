package com.gitty.model

import jakarta.persistence.*

@Entity
@Table(name = "app_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var username: String,

    @Column(nullable = true)
    var password: String? = null,

    var email: String? = null,

    @Column(name = "github_id")
    var githubId: String? = null,

    @Column(name = "github_avatar")
    var githubAvatar: String? = null,

    @Column(name = "github_access_token")
    var githubAccessToken: String? = null,

    @Column(name = "provider")
    var provider: String? = null,

    var createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)