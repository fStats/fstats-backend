package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class OnlineMode(
    val value: Boolean?,
    val count: Int
)
