package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.Database.dataStore
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {

    override fun add(metrics: Metrics): Int {

        try {
            dataStore().connection.use { connection ->

                val statement = connection.createStatement()

                val batchValues = metrics.projectIds.map { (projectId, modVersion) ->
                    "('${Timestamp.from(Instant.now())}', $projectId, ${metrics.metric.isOnlineMode}, '${metrics.metric.minecraftVersion}', '$modVersion', '${metrics.metric.os}', '${metrics.metric.location}')"
                }

                val batchSql =
                    "INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location) VALUES ${
                        batchValues.joinToString(",")
                    }"

                statement.execute(batchSql)
                return SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
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
}