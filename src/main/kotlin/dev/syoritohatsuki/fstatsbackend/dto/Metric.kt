package dev.syoritohatsuki.fstatsbackend.dto

import java.sql.Timestamp

data class Metric(
    val time: Timestamp,
    val projectId: Int,
    val isServer: Boolean,
    val minecraftVersion: String,
    val isOnlineMode: Boolean,
    val modVersion: String,
    val os: Char,
    val location: String
)
