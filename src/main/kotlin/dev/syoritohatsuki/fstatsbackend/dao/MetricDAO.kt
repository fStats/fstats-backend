package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric

interface MetricDAO {
    fun add(metric: Metric): Int
    fun getAll(): Set<Metric>
    fun getLastHalfYearById(projectId: Int): Set<Metric>
}