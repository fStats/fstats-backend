package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.User

interface UserDAO {
    fun create(user: User): Int
    fun deleteById(id: Int): Int
    fun getById(id: Int): User?
    fun getByName(username: String): User?
    fun updateUserData(userId: Int, user: User): Int
}