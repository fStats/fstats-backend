package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Deprecated("Dropped in V2 for unknown time")
@Serializable
data class Exception(
    val timestampSeconds: Long,
    val projectId: Int,
    val message: String
)
