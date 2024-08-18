package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object FavoritesTable : IntIdTable("favorites") {
    val userId = integer("user_id").references(UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val projectId = integer("project_id").references(ProjectsTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
}