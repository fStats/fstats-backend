package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Metric(
    val timestampSeconds: Long? = null,
    val projectId: Int,
    val isServer: Boolean,
    val minecraftVersion: String,
    val isOnlineMode: Boolean? = null,
    val modVersion: String,
    val os: Char,
    val location: String
)
