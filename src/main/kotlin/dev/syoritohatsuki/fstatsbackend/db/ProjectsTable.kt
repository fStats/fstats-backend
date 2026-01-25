package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ProjectsTable : IntIdTable("projects") {
    val name = text("name")
    val ownerId = integer("owner_id").references(UsersTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val isHidden = bool("is_hidden").default(true)
    val hidingReason = text("hiding_reason").nullable().default(null)
}
