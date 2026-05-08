package com.gitty.controller

import com.gitty.dto.CreateProjectRequest
import com.gitty.dto.ProjectDto
import com.gitty.dto.UpdateProjectRequest
import com.gitty.service.CommitService
import com.gitty.service.ProjectService
import com.gitty.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val userService: UserService,
    private val commitService: CommitService
) {

    @GetMapping
    fun getUserProjects(authentication: Authentication): ResponseEntity<List<ProjectDto>> {
        val userId = getUserIdFromAuth(authentication) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return ResponseEntity.ok(projectService.getAllByUserId(userId))
    }

    @PostMapping
    fun createProject(
        @RequestBody request: CreateProjectRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectDto> {
        val userId = getUserIdFromAuth(authentication) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val created = projectService.create(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: Long,
        @RequestBody request: UpdateProjectRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectDto> {
        val userId = getUserIdFromAuth(authentication) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val updated = projectService.update(id, request, userId)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val userId = getUserIdFromAuth(authentication) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return if (projectService.delete(id, userId)) ResponseEntity.noContent().build()
        else ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @PostMapping("/{id}/sync")
    fun syncCommits(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val userId = getUserIdFromAuth(authentication)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val project = projectService.getById(id, userId)
            ?: return ResponseEntity.notFound().build()
        val user = userService.findByUsername(authentication.name)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val token = user.githubAccessToken
        if (token.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "GitHub token not configured"))
        }

        val syncedCount = commitService.syncCommits(id, token)
        return ResponseEntity.ok(mapOf("synced" to syncedCount))
    }

    private fun getUserIdFromAuth(authentication: Authentication): Long? {
        val username = authentication.name
        val user = userService.findByUsername(username) ?: return null
        return user.id
    }
}