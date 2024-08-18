package dev.syoritohatsuki.fstatsbackend.repository

import dev.syoritohatsuki.fstatsbackend.dto.Project

interface ProjectRepository {
    suspend fun create(name: String, ownerId: Int): Int
    suspend fun deleteById(id: Int): Int
    suspend fun getAll(): List<Project>
    suspend fun getByOwner(ownerId: Int): List<Project>
    suspend fun getById(id: Int): Project?
}