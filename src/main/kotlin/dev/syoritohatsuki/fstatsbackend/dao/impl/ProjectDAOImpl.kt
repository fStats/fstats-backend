package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ProjectDAO
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.query
import dev.syoritohatsuki.fstatsbackend.mics.update

object ProjectDAOImpl : ProjectDAO {
    override fun create(name: String, ownerId: Int): Int =
        update("INSERT INTO projects(name, owner_id) VALUES('$name', '$ownerId')")

    override fun deleteById(id: Int): Int = update("DELETE FROM projects WHERE id IN($id)")

    override fun getAll(): List<Project> {
        return mutableListOf<Project>().apply {
            query("SELECT * FROM projects") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Project(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("owner_id")
                        )
                    )
                }
            }
        }
    }

    override fun getByOwner(ownerId: Int): List<Project> {
        return mutableListOf<Project>().apply {
            query("SELECT * FROM projects WHERE owner_id IN($ownerId)") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Project(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("owner_id")
                        )
                    )
                }
            }
        }
    }

    override fun getById(id: Int): Project? {
        var project: Project? = null

        query("SELECT * FROM projects WHERE id IN($id) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                project = Project(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("owner_id")
                )
            }
        }

        return project
    }
}