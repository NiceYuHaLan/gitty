package com.gitty.service

import com.gitty.dto.RegisterRequest
import com.gitty.model.User
import com.gitty.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun create(request: RegisterRequest): User {
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email
        )
        return repository.save(user)
    }

    fun findByUsername(username: String): User? {
        return repository.findByUsername(username).orElse(null)
    }

    fun existsByUsername(username: String): Boolean {
        return repository.existsByUsername(username)
    }

    fun update(id: Long, updatedUser: User): User {
        val existingUser = repository.findById(id)
            .orElseThrow { RuntimeException("User not found") }

        if (updatedUser.username != existingUser.username) {
            if (repository.findByUsername(updatedUser.username).isPresent) {
                throw RuntimeException("Username already exists")
            }
        }

        return repository.save(updatedUser)
    }
}