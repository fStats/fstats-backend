package dev.syoritohatsuki.fstatsbackend.dto

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int = -1,
    val name: String = "",
    val owner: ProjectOwner = ProjectOwner()
) {
    @Serializable
    data class ProjectOwner(
        val id: Int = -1,
        val username: String = ""
    )
}
