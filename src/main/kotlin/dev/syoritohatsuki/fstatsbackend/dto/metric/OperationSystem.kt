package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class OperationSystem(
    val value: Char,
    val count: Int
)
