package dev.syoritohatsuki.fstatsbackend.dto

import java.sql.Timestamp

data class Exception(
    val time: Timestamp,
    val projectId: Int,
    val message: String
)
