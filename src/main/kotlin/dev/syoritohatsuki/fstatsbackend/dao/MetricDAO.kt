package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetric

interface MetricDAO {
    fun add(metric: Metric, country: String): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getLastHalfYearById(projectId: Int): List<Metric>
    fun removeByProjectId(projectId: Int): Pair<String, Int>


    fun getSideById(projectId: Int): List<PieMetric>
    fun getMcVersionById(projectId: Int): List<PieMetric>
    fun getOnlineModeById(projectId: Int): List<PieMetric>
    fun getModVersionById(projectId: Int): List<PieMetric>
    fun getOsById(projectId: Int): List<PieMetric>
    fun getLocationById(projectId: Int): List<PieMetric>
}