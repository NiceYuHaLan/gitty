package com.gitty.controller

import com.gitty.dto.CreateProjectRequest
import com.gitty.dto.ProjectDto
import com.gitty.dto.UpdateProjectRequest
import com.gitty.security.JwtUtil
import com.gitty.service.ProjectService
import com.gitty.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val service: ProjectService,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) {

    @GetMapping
    fun getAllProjects(@RequestHeader("Authorization") authHeader: String): ResponseEntity<List<ProjectDto>> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        return ResponseEntity.ok(service.getAllByUserId(userId))
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String): ResponseEntity<ProjectDto?> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        return ResponseEntity.ok(service.getById(id, userId))
    }

    @PostMapping
    fun createProject(
        @RequestBody request: CreateProjectRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ProjectDto> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        val project = service.create(request, userId)

        if (!request.repoUrl.isNullOrBlank()) {
            service.generateMockDocumentation(project.id)
        }

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(project)
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: Long,
        @RequestBody request: UpdateProjectRequest,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ProjectDto?> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        val updated = service.update(id, request, userId)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        val deleted = service.delete(id, userId)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }

    @GetMapping("/{id}/documentation")
    fun getDocumentation(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String): ResponseEntity<String> {
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)

        val userId = userRepository.findByUsername(username)
            .orElseThrow { RuntimeException("User not found") }
            .id!!

        val project = service.getById(id, userId)
        return ResponseEntity.ok(project?.documentation ?: "Документация еще не сгенерирована.")
    }
}