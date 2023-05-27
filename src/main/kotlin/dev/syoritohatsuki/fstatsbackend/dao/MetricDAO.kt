package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric

interface MetricDAO {
    fun add(metrics: Set<Metric>): Int
    fun getAll(): Set<Metric>
    fun getLastHalfYearById(projectId: Int): Set<Metric>
}