package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.dao.id.IntIdTable

object UsersTable : IntIdTable("users") {
    val username = text("username")
    val passwordHash = text("password_hash")
}
