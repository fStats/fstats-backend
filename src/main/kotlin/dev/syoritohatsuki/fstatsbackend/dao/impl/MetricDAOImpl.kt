package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.mics.query
import dev.syoritohatsuki.fstatsbackend.mics.update
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {
    override fun add(metric: Metric): Int = update(
        "INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location) VALUES('${
            Timestamp.from(Instant.now())
        }', '${metric.projectId}', '${metric.isOnlineMode}', '${metric.minecraftVersion}', '${metric.modVersion}', '${metric.os}', '${metric.location}')"
    )

    override fun getAll(): Set<Metric> = mutableSetOf<Metric>().apply {
        query("SELECT * FROM metrics") { resultSet ->
            while (resultSet.next()) {
                add(
                    Metric(
                        resultSet.getTimestamp("time").time,
                        resultSet.getInt("project_id"),
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

    override fun removeByProjectId(projectId: Int): Int =
        update("DELETE FROM exceptions WHERE project_id IN($projectId)")

}