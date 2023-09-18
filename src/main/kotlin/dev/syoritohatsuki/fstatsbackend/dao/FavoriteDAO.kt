package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Project

interface FavoriteDAO {
    fun getUserFavorites(userId: Int): List<Project>
    fun addProjectToFavorites(userId: Int, projectId: Int): Int
    fun removeProjectFromFavorites(userId: Int, projectId: Int): Int
}