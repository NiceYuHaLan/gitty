package com.gitty.dto

data class CommitAnalysisResponse(
    val summary: String,
    val sentiment: String,
    val tags: String,
    val risks: String
)