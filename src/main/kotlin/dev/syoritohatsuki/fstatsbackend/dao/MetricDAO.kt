package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.ProjectMetric
import dev.syoritohatsuki.fstatsbackend.dto.ProjectMetrics

interface MetricDAO {
    fun add(metrics: Metrics): Int
    fun getLastHalfYearById(projectId: Int): ProjectMetrics?
    fun getMetricCountById(projectId: Int): ProjectMetric?
}