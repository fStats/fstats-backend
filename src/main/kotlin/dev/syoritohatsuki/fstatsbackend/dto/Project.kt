package dev.syoritohatsuki.fstatsbackend.dto

import dev.syoritohatsuki.fstatsbackend.db.ProjectsTable
import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Project(
    val id: Int = -1,
    val name: String = "",
    val isVisible: Boolean? = true,
    val owner: ProjectOwner = ProjectOwner()
) {
    @Serializable
    data class ProjectOwner(
        val id: Int = -1,
        val username: String = ""
    )

    companion object {
        fun fromResultRow(resultRow: ResultRow): Project = Project(
            id = resultRow[ProjectsTable.id].value,
            name = resultRow[ProjectsTable.name],
            isVisible = resultRow[ProjectsTable.isVisible],
            owner = ProjectOwner(
                id = resultRow[ProjectsTable.ownerId], username = resultRow[UsersTable.username]
            )
        )
    }
}
