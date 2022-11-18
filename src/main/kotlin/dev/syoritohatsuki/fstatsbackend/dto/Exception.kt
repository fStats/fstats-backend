package dev.syoritohatsuki.fstatsbackend.dto

data class Exception(
    val timestampSeconds: Long? = null,
    val projectId: Int,
    val message: String
)
