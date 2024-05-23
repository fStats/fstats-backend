package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.ProjectMetric

interface MetricDAO {
    fun add(metrics: Metrics): Int
    fun getLastHalfYearById(projectId: Int): Map<String, Int>
    fun getMetricInDateRange(projectId: Int, from: Long?, to: Long): Map<Long, Int>
    fun getMetricCountById(projectId: Int): ProjectMetric?
}