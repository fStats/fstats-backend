package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ProjectsTable : IntIdTable("projects") {
    val name = text("name")
    val ownerId = integer("owner_id").references(UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val isVisible = bool("visible")
}
