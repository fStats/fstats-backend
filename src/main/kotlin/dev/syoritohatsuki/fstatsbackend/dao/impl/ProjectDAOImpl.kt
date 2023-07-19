package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ProjectDAO
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import dev.syoritohatsuki.fstatsbackend.mics.Database.update

object ProjectDAOImpl : ProjectDAO {
    override fun create(name: String, ownerId: Int): Int =
        update("INSERT INTO projects(name, owner_id) VALUES('$name', '$ownerId')")

    override fun deleteById(id: Int): Int = update("DELETE FROM projects WHERE id IN($id)")

    override fun getAll(): List<Project> = mutableListOf<Project>().apply {
        query("SELECT projects.id, projects.name, projects.owner_id, users.username FROM projects JOIN users ON projects.owner_id = users.id") { resultSet ->
            while (resultSet.next()) {
                add(
                    Project(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        Project.ProjectOwner(
                            resultSet.getInt("owner_id"),
                            resultSet.getString("username")
                        )
                    )
                )
            }
        }
    }

    override fun getByOwner(ownerId: Int): List<Project> = mutableListOf<Project>().apply {
        query("SELECT projects.id, projects.name, projects.owner_id, users.username FROM projects JOIN users ON projects.owner_id = users.id WHERE projects.owner_id IN($ownerId)") { resultSet ->
            while (resultSet.next()) {
                add(
                    Project(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        Project.ProjectOwner(
                            resultSet.getInt("owner_id"),
                            resultSet.getString("username")
                        )
                    )
                )
            }
        }
    }

    override fun getById(id: Int): Project? {
        var project: Project? = null

        query("SELECT projects.id, projects.name, projects.owner_id, users.username FROM projects JOIN users ON projects.owner_id = users.id WHERE projects.id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                project = Project(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    Project.ProjectOwner(
                        resultSet.getInt("owner_id"),
                        resultSet.getString("username")
                    )
                )
            }
        }

        return project
    }
}