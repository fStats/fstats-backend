package dev.syoritohatsuki.fstatsbackend.dto.metric

import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersion(
    val version: String,
    val count: Int
)