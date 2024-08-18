package dev.syoritohatsuki.fstatsbackend.repository.postgre

import dev.syoritohatsuki.fstatsbackend.db.FavoritesTable
import dev.syoritohatsuki.fstatsbackend.db.ProjectsTable
import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.FAILED
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.dbQuery
import dev.syoritohatsuki.fstatsbackend.repository.FavoriteRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object PostgresFavoriteRepository : FavoriteRepository {
    override suspend fun getUserFavorites(userId: Int): List<Project> = dbQuery {
        (ProjectsTable innerJoin FavoritesTable innerJoin UsersTable).selectAll()
            .where { FavoritesTable.userId eq userId }.map {
                Project(
                    id = it[ProjectsTable.id].value, name = it[ProjectsTable.name], owner = Project.ProjectOwner(
                        id = it[ProjectsTable.ownerId], username = it[UsersTable.username]
                    )
                )
            }
    }

    override suspend fun addProjectToFavorites(userId: Int, projectId: Int): Int = dbQuery {
        val insertResult = FavoritesTable.insert {
            it[FavoritesTable.userId] = userId
            it[FavoritesTable.projectId] = projectId
        }
        if (insertResult.insertedCount > 0) SUCCESS else FAILED
    }

    override suspend fun removeProjectFromFavorites(userId: Int, projectId: Int): Int = dbQuery {
        val deleteResult = FavoritesTable.deleteWhere {
            (FavoritesTable.userId eq userId) and (FavoritesTable.projectId eq projectId)
        }
        if (deleteResult > 0) SUCCESS else FAILED
    }
}