package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric

interface MetricDAO {
    fun add(metrics: Set<Metric>): Int
    fun getLastHalfYearById(projectId: Int): Set<Metric>
}