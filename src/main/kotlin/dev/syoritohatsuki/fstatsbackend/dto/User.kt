package dev.syoritohatsuki.fstatsbackend.dto

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String
)
