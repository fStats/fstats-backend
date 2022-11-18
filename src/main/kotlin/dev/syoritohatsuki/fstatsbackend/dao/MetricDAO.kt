package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric

interface MetricDAO {
    fun add(metric: Metric): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getLastHalfYearById(projectId: Int): List<Metric>
    fun removeByProjectId(projectId: Int): Pair<String, Int>
}