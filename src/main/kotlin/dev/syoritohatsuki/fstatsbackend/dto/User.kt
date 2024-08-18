package dev.syoritohatsuki.fstatsbackend.dto

import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class User(
    val id: Int = -1,
    val username: String = "",
    val password: String = "",
    val passwordHash: String = ""
) {
    fun getWithoutPassword(): User = User(id, username)

    companion object {
        fun fromResultRow(resultRow: ResultRow): User = User(
            id = resultRow[UsersTable.id].value,
            username = resultRow[UsersTable.username],
            passwordHash = resultRow[UsersTable.passwordHash]
        )
    }
}
