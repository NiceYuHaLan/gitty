package com.gitty.controller

import com.gitty.dto.CreateProjectRequest
import com.gitty.dto.ProjectDto
import com.gitty.dto.UpdateProjectRequest
import com.gitty.security.JwtUtil
import com.gitty.service.ProjectService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.*
import java.util.UUID

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = ["http://localhost:5173"])
class ProjectController(
    private val service: ProjectService,
    private val jwtUtil: JwtUtil
) {

    private val uploadDir = System.getProperty("user.dir") + "/uploads"

    init {
        Files.createDirectories(Paths.get(uploadDir))
    }

    private fun getUserIdFromToken(authHeader: String?): Long {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw RuntimeException("Unauthorized")
        }
        val token = authHeader.replace("Bearer ", "")
        val username = jwtUtil.getUsernameFromToken(token)
        return service.getUserIdByUsername(username)
    }

    @GetMapping
    fun getAll(@RequestHeader("Authorization") authHeader: String): ResponseEntity<List<ProjectDto>> {
        val userId = getUserIdFromToken(authHeader)
        return ResponseEntity.ok(service.getAllByUserId(userId))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String): ResponseEntity<ProjectDto> {
        val userId = getUserIdFromToken(authHeader)
        val project = service.getById(id, userId)
        return if (project != null) {
            ResponseEntity.ok(project)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun create(
        @RequestParam name: String,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) repoUrl: String?,
        @RequestParam(required = false) image: MultipartFile?,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ProjectDto> {
        val userId = getUserIdFromToken(authHeader)
        val imageUrl = image?.let { saveImage(it) } ?: "https://via.placeholder.com/300x150"
        val request = CreateProjectRequest(name, imageUrl, description, repoUrl)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.create(request, userId))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) repoUrl: String?,
        @RequestParam(required = false) image: MultipartFile?,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ProjectDto> {
        val userId = getUserIdFromToken(authHeader)
        val imageUrl = image?.let { saveImage(it) }
        val request = UpdateProjectRequest(name, imageUrl, description, repoUrl)
        val updated = service.update(id, request, userId)
        return if (updated != null) {
            ResponseEntity.ok(updated)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String): ResponseEntity<Void> {
        val userId = getUserIdFromToken(authHeader)
        val deleted = service.delete(id, userId)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/images/{filename}")
    fun getImage(@PathVariable filename: String): ResponseEntity<ByteArray> {
        val path = Paths.get(uploadDir, filename)
        return if (Files.exists(path)) {
            val contentType = Files.probeContentType(path) ?: "image/jpeg"
            ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(Files.readAllBytes(path))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun saveImage(file: MultipartFile): String {
        val filename = "${UUID.randomUUID()}_${file.originalFilename}"
        val path = Paths.get(uploadDir, filename)
        Files.copy(file.inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        return "/api/projects/images/$filename"
    }

    @GetMapping("/{id}/documentation")
    fun getDocumentation(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<String> {
        val userId = getUserIdFromToken(authHeader)
        val project = service.getById(id, userId)
        return ResponseEntity.ok(project?.documentation ?: "Документация еще не сгенерирована.")
    }
}