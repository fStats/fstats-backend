package dev.syoritohatsuki.fstatsbackend.repository

import dev.syoritohatsuki.fstatsbackend.dto.User

interface UserRepository {
    suspend fun create(user: User): Int
    suspend fun deleteById(id: Int): Int
    suspend fun getById(id: Int): User?
    suspend fun getByName(username: String): User?
    suspend fun updateUserData(userId: Int, user: User): Int
}