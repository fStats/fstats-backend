package dev.syoritohatsuki.fstatsbackend.dao.impl

import de.nycode.bcrypt.hash
import dev.syoritohatsuki.fstatsbackend.dao.UserDAO
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import dev.syoritohatsuki.fstatsbackend.mics.Database.update

object UserDAOImpl : UserDAO {
    override fun create(user: User): Int =
        update("INSERT INTO users(username, password_hash) VALUES('${user.username}', '${user.passwordHash}')")

    override fun deleteById(id: Int): Int = update("DELETE FROM users WHERE id IN($id)")

    override fun getById(id: Int): User? {
        var user: User? = null

        query("SELECT * FROM users WHERE id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                user = User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    passwordHash = resultSet.getString("password_hash")
                )
            }
        }

        return user
    }

    override fun getByName(username: String): User? {
        var user: User? = null

        query("SELECT * FROM users WHERE username = '$username' LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                user = User(
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    passwordHash = resultSet.getString("password_hash")
                )
            }
        }

        return user
    }

    override fun updateUserData(userId: Int, user: User): Int {
        val updates = mutableListOf<String>()

        if (user.username.isNotBlank()) updates.add("username = '${user.username}'")
        if (user.password.isNotBlank()) {
            updates.add("password = '${user.password}'")
            updates.add("password_hash = '${String(hash(user.password))}'")
        }

        if (updates.isEmpty()) return 0

        return update(
            """
                UPDATE users
                SET ${updates.joinToString(", ")}
                WHERE id IN ($userId)
            """.trimIndent()
        )
    }
}