package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.connection
import dev.syoritohatsuki.fstatsbackend.mics.query
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {

    override fun add(metrics: Metrics): Int {

        val connection = connection()

        runCatching {
            connection.autoCommit = false

            val statement = connection.prepareStatement(
                """INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location) 
                        VALUES(?, ?, ?, ?, ?, ?, ?)""".trimMargin()
            )

            connection.use {
                metrics.projectIds.forEach {
                    statement.setTimestamp(1, Timestamp.from(Instant.now()))
                    statement.setInt(2, it.key)
                    statement.setBoolean(3, metrics.metric.isOnlineMode)
                    statement.setString(4, metrics.metric.minecraftVersion)
                    statement.setString(5, it.value)
                    statement.setString(6, metrics.metric.os.toString())
                    statement.setString(7, metrics.metric.location)
                    statement.addBatch()
                }
                statement.executeBatch()
            }
            connection.commit()
            return 1
        }.onFailure {
            connection.rollback()
        }
        return 0
    }

    override fun getLastHalfYearById(projectId: Int): Set<Metric> {
        return mutableSetOf<Metric>().apply {
            query("SELECT * FROM metrics WHERE project_id IN($projectId) AND time > NOW() - INTERVAL '6 months'") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Metric(
                            resultSet.getTimestamp("time").time,
                            projectId,
                            resultSet.getString("minecraft_version"),
                            resultSet.getBoolean("online_mode"),
                            resultSet.getString("mod_version"),
                            resultSet.getCharacterStream("os").read().toChar(),
                            resultSet.getString("location")
                        )
                    )
                }
            }
        }
    }
}