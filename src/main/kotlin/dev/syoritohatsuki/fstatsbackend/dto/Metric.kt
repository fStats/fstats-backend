package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias MetricPie = Map<String, Map<String?, Int>>
typealias MetricLine = Map<String, Int>

@Serializable
data class Metric(
    val timestampSeconds: Long? = null,
    val projectId: Int? = null,
    val minecraftVersion: String = "unknown",
    val isOnlineMode: Boolean,
    val modVersion: String = "",
    val os: Char,
    val location: String = "unknown",
    val fabricApiVersion: String?
)

@Serializable
data class ProjectPieMetric(
    val project: Project,
    @SerialName("metric_pie")
    val metricPie: MetricPie
)

@Serializable
data class ProjectLineMetric(
    val project: Project,
    @SerialName("metric_line")
    val metricLine: MetricLine
)

@Serializable
data class Metrics(
    val projectIds: Map<Int, String>,
    val metric: Metric
)