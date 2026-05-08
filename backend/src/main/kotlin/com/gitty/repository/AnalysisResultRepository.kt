package com.gitty.repository

import com.gitty.model.AnalysisResult
import com.gitty.model.Commit
import org.springframework.data.jpa.repository.JpaRepository

interface AnalysisResultRepository : JpaRepository<AnalysisResult, Long> {
    fun findByCommit(commit: Commit): AnalysisResult?
}