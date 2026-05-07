package com.gitty.model

import jakarta.persistence.*

@Entity
@Table(name = "app_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = true)
    val gitHubId: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    val email: String? = null,

    val password: String? = null,

    @Column(name = "git_hub_access_token")
    val gitHubAccessToken: String? = null
)