package com.gitty.model

import jakarta.persistence.*

@Entity
@Table(name = "analysis_results")
class AnalysisResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne
    @JoinColumn(name = "commit_id", nullable = false)
    var commit: Commit,

    @Column(columnDefinition = "TEXT")
    var summary: String? = null,

    var sentiment: String? = null,
    var tags: String? = null,
    var risks: String? = null
)