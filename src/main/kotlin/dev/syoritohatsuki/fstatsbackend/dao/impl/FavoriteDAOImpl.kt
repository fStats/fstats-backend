package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.FavoriteDAO
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import dev.syoritohatsuki.fstatsbackend.mics.Database.update

object FavoriteDAOImpl : FavoriteDAO {
    override fun getUserFavorites(userId: Int): List<Project> = mutableListOf<Project>().apply {
        query("SELECT Projects.id, Projects.name, Projects.owner_id, Users.username FROM Projects JOIN favorites ON Projects.id IN(favorites.project_id) JOIN Users ON Projects.owner_id IN(Users.id) WHERE favorites.user_id IN($userId)") { resultSet ->
            while (resultSet.next()) {
                add(
                    Project(
                        resultSet.getInt("id"), resultSet.getString("name"), Project.ProjectOwner(
                            resultSet.getInt("owner_id"), resultSet.getString("username")
                        )
                    )
                )
            }
        }
    }

    override fun addProjectToFavorites(userId: Int, projectId: Int): Int =
        update("INSERT INTO favorites(user_id, project_id) VALUES ($userId, $projectId)")

    override fun removeProjectFromFavorites(userId: Int, projectId: Int): Int =
        update("DELETE FROM favorites WHERE user_id = $userId AND project_id = $projectId")
}