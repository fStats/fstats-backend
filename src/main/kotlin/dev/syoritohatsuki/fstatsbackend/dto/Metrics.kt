package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable
import java.util.*

typealias MetricPie = Map<String, Map<String?, Int>>

@Serializable
data class MetricLine(
    val timestamps: List<Long>,
    val counts: List<Int>,
)

@Serializable
data class Metrics(
    val projectIds: Map<Int, String>,
    val metric: Metric
) {
    @Serializable
    data class Metric(
        val uuid: String = UUID.randomUUID().toString(),
        val timestampSeconds: Long? = null,
        val projectId: Int? = null,
        val minecraftVersion: String = "unknown",
        val isOnlineMode: Boolean?,
        val modVersion: String = "",
        val os: Char,
        val location: String = "unknown",
        val fabricApiVersion: String?,
        val isServerSide: Boolean? = true
    ) {
        fun getSubject() = StringBuilder().apply {
            append("fstats.")
            append("metrics.")
            append("${projectId}.")
            append("${timestampSeconds}.")
            append("${location}.")
            append(uuid)
        }.toString()
    }
}