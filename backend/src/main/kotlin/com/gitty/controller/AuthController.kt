package com.gitty.controller

import com.gitty.dto.*
import com.gitty.security.JwtUtil
import com.gitty.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:5173"])
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userService.existsByUsername(request.username)) {
            return ResponseEntity.badRequest().build()
        }

        val user = userService.create(request)
        val token = jwtUtil.generateToken(request.username)

        return ResponseEntity.ok(AuthResponse(token, request.username, user.id!!))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val user = userService.findByUsername(request.username)

        if (user == null || !passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.status(401).build()
        }

        val token = jwtUtil.generateToken(request.username)
        return ResponseEntity.ok(AuthResponse(token, request.username, user.id!!))
    }

    @GetMapping("/me")
    fun getCurrentUser(@RequestHeader("Authorization") authHeader: String): ResponseEntity<AuthResponse> {
        val token = authHeader.replace("Bearer ", "")

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build()
        }

        val username = jwtUtil.getUsernameFromToken(token)
        val user = userService.findByUsername(username)

        return user?.let {
            ResponseEntity.ok(AuthResponse(token, it.username, it.id!!))
        } ?: ResponseEntity.status(401).build()
    }
}