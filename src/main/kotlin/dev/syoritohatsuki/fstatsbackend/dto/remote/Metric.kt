package dev.syoritohatsuki.fstatsbackend.dto.remote

import java.sql.Timestamp
import java.time.LocalDateTime

data class Metric(
    val time: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val projectId: Int,
    val isServer: Boolean,
    val minecraftVersion: String,
    val isOnlineMode: Boolean,
    val modVersion: String,
    val os: Char,
    val location: String
)
