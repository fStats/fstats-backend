package dev.syoritohatsuki.fstatsbackend.dto

data class Project(
    val id: Int = -1,
    val name: String,
    val ownerId: Int
)
