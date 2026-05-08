package com.gitty.controller

import com.gitty.model.Commit
import com.gitty.repository.AnalysisResultRepository
import com.gitty.repository.CommitRepository
import com.gitty.service.CommitService
import com.gitty.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/commits")
class CommitController(
    private val commitService: CommitService,
    private val userService: UserService,
    private val analysisResultRepository: AnalysisResultRepository,
    private val commitRepository: CommitRepository
) {

    @GetMapping("/project/{projectId}")
    fun getCommitsByProject(@PathVariable projectId: Long): ResponseEntity<List<CommitDto>> {
        val commits = commitService.getCommitsByProject(projectId)
        val dtos = commits.map { commit ->
            val analysis = analysisResultRepository.findByCommit(commit)
            CommitDto(
                id = commit.id,
                sha = commit.sha,
                authorName = commit.authorName,
                authorEmail = commit.authorEmail,
                commitDate = commit.commitDate?.toString(),
                message = commit.message,
                url = commit.url,
                processed = commit.processed,
                analysis = analysis?.let {
                    AnalysisDto(
                        summary = it.summary,
                        sentiment = it.sentiment,
                        tags = it.tags,
                        risks = it.risks
                    )
                }
            )
        }
        return ResponseEntity.ok(dtos)
    }

    private fun getUserIdFromAuth(authentication: Authentication): Long? {
        val email = authentication.name
        val user = userService.findByEmail(email) ?: return null
        return user.id
    }

    @GetMapping("/{commitId}/analysis")
    fun getAnalysisByCommit(
        @PathVariable commitId: Long,
        authentication: Authentication
    ): ResponseEntity<AnalysisDto> {
        val userId = getUserIdFromAuth(authentication) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val commit = commitRepository.findById(commitId).orElse(null) ?: return ResponseEntity.notFound().build()

        val project = commit.project
        if (project.userId != userId) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val analysis = analysisResultRepository.findByCommit(commit)
        return if (analysis != null) {
            ResponseEntity.ok(
                AnalysisDto(
                    summary = analysis.summary,
                    sentiment = analysis.sentiment,
                    tags = analysis.tags,
                    risks = analysis.risks
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

data class CommitDto(
    val id: Long,
    val sha: String,
    val authorName: String?,
    val authorEmail: String?,
    val commitDate: String?,
    val message: String?,
    val url: String?,
    val processed: Boolean,
    val analysis: AnalysisDto?
)

data class AnalysisDto(
    val summary: String?,
    val sentiment: String?,
    val tags: String?,
    val risks: String?
)