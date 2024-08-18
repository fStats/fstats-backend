package dev.syoritohatsuki.fstatsbackend.repository

import dev.syoritohatsuki.fstatsbackend.dto.Project

interface FavoriteRepository {
    suspend fun getUserFavorites(userId: Int): List<Project>
    suspend fun addProjectToFavorites(userId: Int, projectId: Int): Int
    suspend fun removeProjectFromFavorites(userId: Int, projectId: Int): Int
}