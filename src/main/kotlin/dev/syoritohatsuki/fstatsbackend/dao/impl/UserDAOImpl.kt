package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.UserDAO
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection

object UserDAOImpl : UserDAO {
    override fun create(user: User) {
        try {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("INSERT INTO users(username, password_hash) VALUES('${user.username}', '${user.passwordHash}')")
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun deleteById(id: Int) {
        try {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("DELETE FROM users WHERE id IN($id)")
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun getAll(): List<User> {
        return mutableListOf<User>().apply {
            try {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM users").use { resultSet ->
                            while (resultSet.next()) {
                                add(
                                    User(
                                        resultSet.getInt("id"),
                                        resultSet.getString("username"),
                                        resultSet.getBytes("password_hash")
                                    )
                                )
                            }
                            connection.close(statement, resultSet)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override fun getById(id: Int): User? {
        var user: User? = null
        try {
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
        } catch (e: Exception) {
            println(e.message)
        }
        return user
    }
}