package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ProjectDAO
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection

object ProjectDAOImpl : ProjectDAO {
    override fun create(project: Project) {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("INSERT INTO projects(name, owner_id) VALUES('${project.name}', '${project.ownerId}')")
                }
            }
        }.onFailure { println(it.message) }
    }

    override fun deleteById(id: Int) {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("DELETE FROM projects WHERE id IN($id)")
                }
            }
        }.onFailure { println(it.message) }
    }

    override fun getAll(): List<Project> {
        return mutableListOf<Project>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM projects").use { resultSet ->
                            while (resultSet.next()) {
                                add(
                                    Project(
                                        resultSet.getInt("id"),
                                        resultSet.getString("name"),
                                        resultSet.getInt("owner_id")
                                    )
                                )
                            }
                            connection.close(statement, resultSet)
                        }
                    }
                }
            }.onFailure { println(it.message) }
        }
    }

    override fun getByOwner(ownerId: Int): List<Project> {
        return mutableListOf<Project>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM projects WHERE owner_id IN($ownerId)").use { resultSet ->
                            while (resultSet.next()) {
                                add(
                                    Project(
                                        resultSet.getInt("id"),
                                        resultSet.getString("name"),
                                        resultSet.getInt("owner_id")
                                    )
                                )
                            }
                            connection.close(statement, resultSet)
                        }
                    }
                }
            }.onFailure { println(it.message) }
        }
    }

    override fun getById(id: Int): Project? {
        var project: Project? = null
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT * FROM projects WHERE id IN($id) LIMIT 1").use { resultSet ->
                        while (resultSet.next()) {
                            project = Project(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getInt("owner_id")
                            )
                        }
                        connection.close(statement, resultSet)
                    }
                }
            }
        }.onFailure { println(it.message) }
        return project
    }
}