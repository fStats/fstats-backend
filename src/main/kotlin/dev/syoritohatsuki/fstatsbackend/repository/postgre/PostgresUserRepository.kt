package dev.syoritohatsuki.fstatsbackend.repository.postgre

import de.nycode.bcrypt.hash
import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.FAILED
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.dbQuery
import dev.syoritohatsuki.fstatsbackend.repository.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

object PostgresUserRepository : UserRepository {
    override suspend fun create(user: User): Int = dbQuery {
        val insertResult = UsersTable.insert {
            it[username] = user.username
            it[passwordHash] = user.passwordHash
        }
        if (insertResult.insertedCount > 0) SUCCESS else FAILED
    }

    override suspend fun deleteById(id: Int): Int = dbQuery {
        val deleteResult = UsersTable.deleteWhere { UsersTable.id eq id }
        if (deleteResult > 0) SUCCESS else FAILED
    }

    override suspend fun getById(id: Int): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.mapNotNull {
            User(
                id = it[UsersTable.id].value,
                username = it[UsersTable.username],
                passwordHash = it[UsersTable.passwordHash]
            )
        }.singleOrNull()
    }

    override suspend fun getByName(username: String): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.username eq username }.mapNotNull {
            User(
                id = it[UsersTable.id].value,
                username = it[UsersTable.username],
                passwordHash = it[UsersTable.passwordHash]
            )
        }.singleOrNull()
    }

    override suspend fun updateUserData(userId: Int, user: User): Int {
        if (user.username.isBlank() && user.password.isBlank()) return FAILED

        return dbQuery {
            UsersTable.update({ UsersTable.id eq userId }) {
                if (user.username.isNotBlank()) it[username] = user.username
                if (user.password.isNotBlank()) it[passwordHash] = String(hash(user.password))
            }
        }
    }
}