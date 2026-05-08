package com.gitty.repository

import com.gitty.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun findByEmail(email: String): Optional<User>
    fun findByGithubId(githubId: String): Optional<User>
}