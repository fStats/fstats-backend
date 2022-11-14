package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ProjectDAO
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection
import java.sql.SQLException

object ProjectDAOImpl : ProjectDAO {
    override fun create(project: Project): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair(
                        "Project created",
                        statement.executeUpdate("INSERT INTO projects(name, owner_id) VALUES('${project.name}', '${project.ownerId}')")
                    )
                }
            }
        }.onFailure {
            (it as SQLException).apply {
                return Pair(it.localizedMessage, it.errorCode)
            }
        }
        return Pair("Offline", -1)
    }

    override fun deleteById(id: Int): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair("Project deleted", statement.executeUpdate("DELETE FROM projects WHERE id IN($id)"))
                }
            }
        }.onFailure {
            (it as SQLException).apply {
                return Pair(it.localizedMessage, it.errorCode)
            }
        }
        return Pair("Offline", -1)
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
            }.onFailure { println(it.localizedMessage) }
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
        }.onFailure { println(it.localizedMessage) }
        return project
    }
}