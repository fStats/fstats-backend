package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.mics.TimeUnit

interface MetricDAO {
    fun add(metric: Metric): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getLatest(interval: Int, timeUnit: TimeUnit): List<Metric>
}