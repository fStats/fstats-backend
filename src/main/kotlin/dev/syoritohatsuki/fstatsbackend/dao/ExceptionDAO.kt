package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import java.sql.Timestamp

//TODO Implement Required
interface ExceptionDAO {
    fun add(metric: Metric)
    fun getAll(): List<Metric>
    fun getRange(from: Timestamp, to: Timestamp): List<Metric>
}