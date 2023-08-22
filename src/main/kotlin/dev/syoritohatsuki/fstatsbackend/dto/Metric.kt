package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias MetricMap = Map<String, Map<String, Int>>

@Serializable
data class Metric(
    val timestampSeconds: Long? = null,
    val projectId: Int? = null,
    val minecraftVersion: String = "unknown",
    val isOnlineMode: Boolean,
    val modVersion: String = "",
    val os: Char,
    val location: String = "unknown",
    val fabricApiVersion: String = "unknown"
)

@Serializable
data class ProjectMetric(
    val project: Project,
    @SerialName("metric_map")
    val metricMap: MetricMap
)

@Serializable
data class Metrics(
    val projectIds: Map<Int, String>,
    val metric: Metric
)