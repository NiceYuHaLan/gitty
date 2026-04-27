package com.gitty.controller

import com.gitty.model.User
import com.gitty.security.JwtUtil
import com.gitty.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    data class UserProfileResponse(
        val id: Long,
        val username: String,
        val email: String?,
        val githubId: String?,
        val githubAvatar: String?,
        val provider: String?,
        val token: String? = null
    )

    data class UpdateProfileRequest(
        val username: String?,
        val email: String?
    )

    @GetMapping("/me")
    fun getCurrentUser(@RequestHeader("Authorization") authHeader: String): ResponseEntity<UserProfileResponse> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val user = userService.findByUsername(username)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(user.toResponse())
    }

    @PutMapping("/me")
    fun updateProfile(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        val token = authHeader.replace("Bearer ", "")
        val oldUsername = jwtUtil.getUsernameFromToken(token)

        val user = userService.findByUsername(oldUsername)
            ?: return ResponseEntity.notFound().build()

        if (request.username != null && request.username != user.username) {
            if (userService.existsByUsername(request.username)) {
                return ResponseEntity.badRequest().build()
            }
        }

        val updatedUser = user.copy(
            username = request.username ?: user.username,
            email = request.email ?: user.email
        )

        val saved = userService.update(user.id!!, updatedUser)

        // Генерируем НОВЫЙ токен с новым username
        val newToken = jwtUtil.generateToken(saved.username)

        return ResponseEntity.ok(saved.toResponseWithToken(newToken))
    }

    private fun User.toResponse(): UserProfileResponse {
        return UserProfileResponse(
            id = id!!,
            username = username,
            email = email,
            githubId = githubId,
            githubAvatar = githubAvatar,
            provider = provider
        )
    }

    private fun User.toResponseWithToken(token: String): UserProfileResponse {
        return UserProfileResponse(
            id = id!!,
            username = username,
            email = email,
            githubId = githubId,
            githubAvatar = githubAvatar,
            provider = provider,
            token = token
        )
    }
}