package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Metric(
    val timestampSeconds: Long,
    val projectId: Int,
    @Deprecated("Side is removed in V2")
    val side: Boolean,
    val minecraftVersion: String = "unknown",
    val isOnlineMode: Boolean? = null,
    val modVersion: String,
    val os: Char,
    val location: String = "unknown"
)
