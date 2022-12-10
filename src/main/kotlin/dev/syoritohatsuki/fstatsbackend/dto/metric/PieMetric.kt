package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class PieMetric(
    val name: String,
    val y: Int
)
