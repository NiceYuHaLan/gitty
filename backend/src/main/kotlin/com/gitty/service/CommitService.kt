package com.gitty.service

import com.gitty.client.GitHubClient
import com.gitty.model.AnalysisResult
import com.gitty.model.Commit
import com.gitty.model.Project
import com.gitty.repository.AnalysisResultRepository
import com.gitty.repository.CommitRepository
import com.gitty.repository.ProjectRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

@Service
class CommitService(
    private val commitRepository: CommitRepository,
    private val projectRepository: ProjectRepository,
    private val gitHubClient: GitHubClient,
    private val immersAnalysisService: ImmersAnalysisService,
    private val analysisResultRepository: AnalysisResultRepository
) {

    fun syncCommits(projectId: Long, githubToken: String): Int {
        val project = projectRepository.findById(projectId).orElse(null) ?: return 0
        val (owner, repo) = parseRepoUrl(project.repoUrl)

        val commits = gitHubClient.getCommits(owner, repo, "Bearer $githubToken")
        var newCount = 0

        for (commitDto in commits) {
            val existing = commitRepository.findByProjectAndSha(project, commitDto.sha)
            if (existing == null) {
                val commitDate = commitDto.commit.author?.date?.let {
                    try { Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it)) } catch (e: Exception) { null }
                }
                val commit = Commit(
                    project = project,
                    sha = commitDto.sha,
                    authorName = commitDto.commit.author?.name,
                    authorEmail = commitDto.commit.author?.email,
                    commitDate = commitDate,
                    message = commitDto.commit.message,
                    url = commitDto.html_url,
                    processed = false
                )
                commitRepository.save(commit)
                newCount++

                CompletableFuture.runAsync {
                    analyzeCommitWithAi(commit)
                }
            }
        }
        return newCount
    }

    private fun analyzeCommitWithAi(commit: Commit) {
        if (commit.processed) return
        commit.message?.takeIf { it.isNotBlank() }?.let { message ->
            val analysis = immersAnalysisService.analyzeCommit(message)
            if (analysis != null) {
                val analysisResult = AnalysisResult(
                    commit = commit,
                    summary = analysis.summary,
                    sentiment = analysis.sentiment,
                    tags = analysis.tags,
                    risks = analysis.risks
                )
                analysisResultRepository.save(analysisResult)
                commit.processed = true
                commitRepository.save(commit)
                println("✅ AI analysis saved for commit ${commit.sha}")
            } else {
                println("❌ Failed to analyze commit ${commit.sha}")
            }
        }
    }

    fun getCommitsByProject(projectId: Long): List<Commit> {
        val project = projectRepository.findById(projectId).orElse(null) ?: return emptyList()
        return commitRepository.findByProjectOrderByCommitDateDesc(project)
    }

    private fun parseRepoUrl(repoUrl: String?): Pair<String, String> {
        if (repoUrl.isNullOrBlank()) throw IllegalArgumentException("Repo URL is empty")
        val parts = repoUrl.trimEnd('/').split("/")
        val owner = parts[parts.size - 2]
        val repo = parts[parts.size - 1].replace(".git", "")
        return owner to repo
    }
}