package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object FavoritesTable : IntIdTable("favorites") {
    val userId = integer("user_id").references(UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val projectId = integer("project_id").references(ProjectsTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
}