package com.gitty.mapper

import com.gitty.dto.CreateProjectRequest
import com.gitty.dto.ProjectDto
import com.gitty.dto.UpdateProjectRequest
import com.gitty.model.Project
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class ProjectMapper {

    fun toDto(project: Project): ProjectDto = ProjectDto(
        id = project.id!!,
        name = project.name,
        imageUrl = project.imageUrl,
        description = project.description,
        userId = project.userId,
        repoUrl = project.repoUrl,
        documentation = project.documentation,
        createdAt = project.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    fun toEntity(request: CreateProjectRequest, userId: Long): Project = Project(
        name = request.name,
        imageUrl = request.imageUrl,
        description = request.description,
        repoUrl = request.repoUrl,
        documentation = null,
        userId = userId
    )

    fun updateEntity(existing: Project, request: UpdateProjectRequest): Project {
        return Project(
            id = existing.id,
            name = request.name ?: existing.name,
            imageUrl = request.imageUrl ?: existing.imageUrl,
            description = request.description ?: existing.description,
            repoUrl = request.repoUrl ?: existing.repoUrl,
            documentation = request.documentation ?: existing.documentation,
            userId = existing.userId,
            createdAt = existing.createdAt
        )
    }
}