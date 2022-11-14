package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int?,
    val name: String,
    val ownerId: Int
)
