package dev.syoritohatsuki.fstatsbackend.repository

import dev.syoritohatsuki.fstatsbackend.dto.MetricLine
import dev.syoritohatsuki.fstatsbackend.dto.MetricPie
import dev.syoritohatsuki.fstatsbackend.dto.Metrics

interface MetricRepository {
    suspend fun add(metrics: Metrics): Int
    suspend fun getMetricInDateRange(projectId: Int, from: Long?, to: Long): MetricLine
    suspend fun getMetricCountById(projectId: Int): MetricPie
}