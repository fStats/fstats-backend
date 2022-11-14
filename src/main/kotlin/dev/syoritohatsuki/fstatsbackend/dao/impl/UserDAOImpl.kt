package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.UserDAO
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection
import java.sql.SQLException

object UserDAOImpl : UserDAO {
    override fun create(user: User): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair(
                        "User created",
                        statement.executeUpdate("INSERT INTO users(username, password_hash) VALUES('${user.username}', '${user.passwordHash}')")
                    )
                }
            }
        }.onFailure {
            (it as SQLException).apply {
                return Pair(localizedMessage, errorCode)
            }
        }
        return Pair("Offline", -1)
    }

    override fun deleteById(id: Int): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair(
                        "User removed",
                        statement.executeUpdate("DELETE FROM users WHERE id IN($id)")
                    )
                }
            }
        }.onFailure {
            (it as SQLException).apply {
                return Pair(localizedMessage, errorCode)
            }
        }
        return Pair("Offline", -1)
    }

    override fun getById(id: Int): User? {
        var user: User? = null
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT * FROM users WHERE id IN($id) LIMIT 1").use { resultSet ->
                        while (resultSet.next()) {
                            user = User(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getBytes("password_hash")
                            )
                        }
                        connection.close(statement, resultSet)
                    }
                }
            }
        }.onFailure { println(it.localizedMessage) }
        return user
    }
}