package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ExceptionDAO
import dev.syoritohatsuki.fstatsbackend.dto.remote.Exception
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection
import java.sql.SQLException

object ExceptionDAOImpl : ExceptionDAO {
    override fun add(exception: Exception): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair(
                        "Exception added",
                        statement.executeUpdate("INSERT INTO exceptions(time, project_id, message) VALUES('${exception.time}', '${exception.projectId}', '${exception.message}')")
                    )
                }
            }
        }.onFailure {
            (it as SQLException).apply {
                return Pair(localizedMessage, errorCode)
            }
        }
        return Pair("Offline", -1)
    }

    override fun getByProject(projectId: Int): List<Exception> {
        return mutableListOf<Exception>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM exceptions WHERE project_id IN($projectId)")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        Exception(
                                            resultSet.getTimestamp("timestamp"),
                                            resultSet.getInt("project_id"),
                                            resultSet.getString("message")
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
}