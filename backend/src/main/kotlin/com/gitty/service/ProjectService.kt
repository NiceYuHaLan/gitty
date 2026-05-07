package com.gitty.service

import com.gitty.dto.CreateProjectRequest
import com.gitty.dto.ProjectDto
import com.gitty.dto.UpdateProjectRequest
import com.gitty.mapper.ProjectMapper
import com.gitty.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectMapper: ProjectMapper
) {

    @Transactional
    fun create(request: CreateProjectRequest, userId: Long): ProjectDto {
        val entity = projectMapper.toEntity(request, userId)
        val saved = projectRepository.save(entity)

        if (!request.repoUrl.isNullOrBlank()) {
            generateMockDocumentation(saved.id!!)
        }

        return projectMapper.toDto(saved)
    }

    @Transactional
    fun update(id: Long, request: UpdateProjectRequest, userId: Long): ProjectDto? {
        val existing = projectRepository.findById(id).orElse(null) ?: return null
        if (existing.userId != userId) return null

        val updated = projectMapper.updateEntity(existing, request)
        return projectMapper.toDto(projectRepository.save(updated))
    }

    fun getAllByUserId(userId: Long): List<ProjectDto> {
        return projectRepository.findByUserId(userId).map { projectMapper.toDto(it) }
    }

    fun getById(id: Long, userId: Long): ProjectDto? {
        return projectRepository.findById(id)
            .filter { it.userId == userId }
            .map { projectMapper.toDto(it) }
            .orElse(null)
    }

    @Transactional
    fun delete(id: Long, userId: Long): Boolean {
        val project = projectRepository.findById(id).orElse(null) ?: return false
        if (project.userId != userId) return false
        projectRepository.delete(project)
        return true
    }

    internal fun generateMockDocumentation(projectId: Long) {
        val project = projectRepository.findById(projectId).orElseThrow()

        val mockDocs = """
          TEST DOCUMENTATION"""

        projectRepository.save(project.copy(documentation = mockDocs))
    }
}