package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.MetricLine
import dev.syoritohatsuki.fstatsbackend.dto.MetricPie
import dev.syoritohatsuki.fstatsbackend.dto.Metrics

interface MetricDAO {
    fun add(metrics: Metrics): Int
    fun getMetricInDateRange(projectId: Int, from: Long?, to: Long): MetricLine
    fun getMetricCountById(projectId: Int): MetricPie
}