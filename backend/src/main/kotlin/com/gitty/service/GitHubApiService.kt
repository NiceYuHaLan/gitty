package com.gitty.service

import com.gitty.client.GitHubClient
import com.gitty.client.GitHubCommitDto
import org.springframework.stereotype.Service

@Service
class GitHubApiService(
    private val gitHubClient: GitHubClient
) {
    fun fetchCommits(owner: String, repo: String, token: String): List<GitHubCommitDto> {
        return gitHubClient.getCommits(owner, repo, "Bearer $token")
    }
}