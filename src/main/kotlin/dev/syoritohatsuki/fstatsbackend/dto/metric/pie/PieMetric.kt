package dev.syoritohatsuki.fstatsbackend.dto.metric.pie

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
abstract class PieMetric {
    abstract val y: Int
}

@Serializable
data class PieMetricString(
    val name: String,
    override val y: Int
) : PieMetric()

@Serializable
data class PieMetricBoolean(
    val name: Boolean,
    override val y: Int
) : PieMetric()