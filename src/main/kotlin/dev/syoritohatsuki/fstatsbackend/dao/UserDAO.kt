package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.User

interface UserDAO {
    fun create(user: User)
    fun deleteById(id: Int)
    fun getAll(): List<User>
    fun getById(id: Int): User?
}