package dev.syoritohatsuki.fstatsbackend.dto

import java.sql.Timestamp
import java.time.LocalDateTime

data class Exception(
    val time: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val projectId: Int,
    val message: String
)
