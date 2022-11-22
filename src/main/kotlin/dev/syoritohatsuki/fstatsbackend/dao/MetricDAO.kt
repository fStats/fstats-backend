package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric

interface MetricDAO {
    fun add(metric: Metric, country: String): Pair<String, Int>
    fun getAll(isMultiplayer: Boolean): List<Metric>
    fun getLastHalfYearById(projectId: Int, isMultiplayer: Boolean): List<Metric>
    fun removeByProjectId(projectId: Int): Pair<String, Int>
}