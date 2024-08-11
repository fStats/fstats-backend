package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

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
        val timestampSeconds: Long? = null,
        val projectId: Int? = null,
        val minecraftVersion: String = "unknown",
        val isOnlineMode: Boolean?,
        val modVersion: String = "",
        val os: Char,
        val location: String = "unknown",
        val fabricApiVersion: String?,
        val isServerSide: Boolean? = true
    )
}