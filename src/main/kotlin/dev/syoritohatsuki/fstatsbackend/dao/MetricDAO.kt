package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.Metrics

interface MetricDAO {
    fun add(metrics: Metrics): Int
    fun getLastHalfYearById(projectId: Int): Set<Metric>
    fun getMetricCountById(projectId: Int): Map<String, Map<String, Int>>
}