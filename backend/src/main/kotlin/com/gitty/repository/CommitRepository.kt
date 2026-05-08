package com.gitty.repository

import com.gitty.model.Commit
import com.gitty.model.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommitRepository : JpaRepository<Commit, Long> {
    fun findByProjectAndSha(repo: Project, sha: String): Commit?
    fun findByProjectOrderByCommitDateDesc(repo: Project): List<Commit>
    @Query("SELECT c FROM Commit c WHERE c.project = :project AND c.processed = false")
    fun findUnprocessedByProject(project: Project): List<Commit>
}