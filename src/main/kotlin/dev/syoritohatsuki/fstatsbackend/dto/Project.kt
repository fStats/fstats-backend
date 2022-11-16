package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int = -1,
    val name: String = "",
    val ownerId: Int = -1
)
