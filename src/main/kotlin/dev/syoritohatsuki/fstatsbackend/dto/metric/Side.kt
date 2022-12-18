package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class Side(
    val value: Boolean,
    val count: Int
)