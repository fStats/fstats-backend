package dev.syoritohatsuki.fstatsbackend.dto.metric.pie

import kotlinx.serialization.Serializable

@Serializable
data class PieMetricList(
    val side: List<PieMetric>,
    val mcversion: List<PieMetric>,
    val onlinemode: List<PieMetric>,
    val modversion: List<PieMetric>,
    val os: List<PieMetric>,
    val location: List<PieMetric>
)
