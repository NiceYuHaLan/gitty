package com.gitty.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String? = null
)