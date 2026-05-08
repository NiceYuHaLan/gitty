package com.gitty.analytics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class MetricsService(private val meterRegistry: MeterRegistry) {

    private val commitsProcessedCounter = meterRegistry.counter("gitty.commits.processed")
    private val analysisErrorsCounter = meterRegistry.counter("gitty.analysis.errors")

    fun incrementProcessedCommits() {
        commitsProcessedCounter.increment()
    }

    fun incrementAnalysisErrors() {
        analysisErrorsCounter.increment()
    }
}