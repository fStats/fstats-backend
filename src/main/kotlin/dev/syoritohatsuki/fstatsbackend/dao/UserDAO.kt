package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.User

interface UserDAO {
    fun create(user: User): Pair<String, Int>
    fun deleteById(id: Int): Int
    fun getById(id: Int): User?
    fun getByName(username: String): User?
}