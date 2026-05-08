package com.gitty.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "github", url = "https://api.github.com")
interface GitHubClient {

    @GetMapping("/repos/{owner}/{repo}/commits")
    fun getCommits(
        @PathVariable owner: String,
        @PathVariable repo: String,
        @RequestHeader("Authorization") authorization: String
    ): List<GitHubCommitDto>
}

data class GitHubCommitDto(
    val sha: String,
    val commit: CommitDetailDto,
    val html_url: String
)

data class CommitDetailDto(
    val author: AuthorDto?,
    val message: String
)

data class AuthorDto(
    val name: String?,
    val email: String?,
    val date: String?
)