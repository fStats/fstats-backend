package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.ProjectLineMetric
import dev.syoritohatsuki.fstatsbackend.dto.ProjectPieMetric

interface MetricDAO {
    fun add(metrics: Metrics): Int
    fun getLastHalfYearById(projectId: Int): ProjectLineMetric?
    fun getMetricCountById(projectId: Int): ProjectPieMetric?
}