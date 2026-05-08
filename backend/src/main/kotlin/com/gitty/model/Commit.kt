package com.gitty.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "commits", uniqueConstraints = [
    UniqueConstraint(columnNames = ["project_id", "sha"])
])
class Commit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    var project: Project,

    @Column(nullable = false)
    var sha: String,

    var authorName: String?,
    var authorEmail: String?,
    var commitDate: Instant?,
    @Column(length = 10000)
    var message: String?,
    var url: String?,

    var processed: Boolean = false,
    var analyzedAt: Instant? = null
)