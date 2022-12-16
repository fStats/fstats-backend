package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.ExceptionDAO
import dev.syoritohatsuki.fstatsbackend.dto.Exception
import dev.syoritohatsuki.fstatsbackend.mics.query
import dev.syoritohatsuki.fstatsbackend.mics.update
import java.sql.Timestamp
import java.time.Instant

object ExceptionDAOImpl : ExceptionDAO {
    override fun add(exception: Exception): Pair<String, Int> {
        var data = Pair("Offline", -1)

        update("INSERT INTO exceptions(time, project_id, message) VALUES('${Timestamp.from(Instant.now())}', '${exception.projectId}', '${exception.message}')") {
            data = Pair("Exception added", it)
        }

        return data
    }

    override fun getByProject(projectId: Int): List<Exception> {
        return mutableListOf<Exception>().apply {
            query("SELECT * FROM exceptions WHERE project_id IN($projectId)") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Exception(
                            resultSet.getTimestamp("timestamp").time,
                            resultSet.getInt("project_id"),
                            resultSet.getString("message")
                        )
                    )
                }
            }
        }
    }

    override fun removeByProjectId(projectId: Int): Pair<String, Int> {
        var data = Pair("Offline", -1)

        update("DELETE FROM exceptions WHERE project_id IN($projectId)") {
            data = Pair("Exception removed", it)
        }

        return data
    }
}