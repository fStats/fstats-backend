package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.remote.Metric
import dev.syoritohatsuki.fstatsbackend.mics.TimeUnit

interface MetricDAO {
    fun add(metric: Metric): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getLatest(projectId: Int, interval: Int, timeUnit: TimeUnit): List<Metric>
    fun removeByProjectId(projectId: Int): Pair<String, Int>
}