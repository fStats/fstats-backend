package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Exception(
    val timestampSeconds: Long? = null,
    val projectId: Int,
    val message: String
)
