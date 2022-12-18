package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class ModVersion(
    val value: String,
    val count: Int
)
