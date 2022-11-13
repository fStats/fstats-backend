package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import java.sql.Timestamp

//TODO Implement Required
interface MetricDAO {
    fun add(metric: Metric): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getRange(from: Timestamp, to: Timestamp): List<Metric>
}