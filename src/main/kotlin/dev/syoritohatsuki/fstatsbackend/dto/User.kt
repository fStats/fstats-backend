package dev.syoritohatsuki.fstatsbackend.dto

data class User(
    val id: Int = -1,
    val username: String,
    val passwordHash: ByteArray
)
