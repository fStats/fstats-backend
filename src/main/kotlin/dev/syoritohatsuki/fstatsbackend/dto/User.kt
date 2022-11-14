package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val username: String?,
    val passwordHash: ByteArray? = null
)
