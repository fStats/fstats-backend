package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.dto.ProjectMetric
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.Database.dataStore
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant

object MetricDAOImpl : MetricDAO {

    override fun add(metrics: Metrics): Int {
        try {
            dataStore().connection.use { connection ->
                connection.prepareStatement("INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location, fabric_api_version) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
                    .use {
                        metrics.projectIds.forEach { (projectId, modVersion) ->
                            it.setTimestamp(1, Timestamp.from(Instant.now()))
                            it.setInt(2, projectId)
                            it.setBoolean(3, metrics.metric.isOnlineMode)
                            it.setString(4, metrics.metric.minecraftVersion)
                            it.setString(5, modVersion)
                            it.setString(6, metrics.metric.os.toString())
                            it.setString(7, metrics.metric.location)

                            if (metrics.metric.fabricApiVersion != null) {
                                it.setString(8, metrics.metric.fabricApiVersion)
                            } else {
                                it.setNull(8, Types.VARCHAR)
                            }

                            it.addBatch()
                        }
                        it.executeBatch()
                    }
                return SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    override fun getLastHalfYearById(projectId: Int): Map<String, Int> {

        val metrics = mutableMapOf<String, Int>().apply {
            query(
                """
                    SELECT
                        time_bucket('30 minutes', time) AS time_bucket,
                        count(*)::int AS count
                    FROM metrics
                    WHERE time >= NOW() - interval '1 year' AND project_id IN(${projectId})
                    GROUP BY time_bucket
                    ORDER BY time_bucket;
            """
            ) { resultSet ->
                while (resultSet.next()) {
                    this[resultSet.getString("time_bucket")] = resultSet.getInt("count")
                }
            }
        }

        return metrics
    }

    override fun getMetricCountById(projectId: Int): ProjectMetric? {

        var project: Project? = null
        val metricMap = mutableMapOf<String, MutableMap<String?, Int>>().apply {
            query(
                """
                        SELECT 'minecraft_version' AS column_name,
                               COUNT(*)            AS count,
                               minecraft_version   AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY minecraft_version
                        
                        UNION ALL
                        
                        SELECT 'online_mode'             AS column_name,
                               COUNT(*)                  AS count,
                               CAST(online_mode AS TEXT) AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY online_mode
                        
                        UNION ALL
                        
                        SELECT 'mod_version' AS column_name,
                               COUNT(*)      AS count,
                               mod_version   AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY mod_version
                        
                        UNION ALL
                        
                        SELECT 'os'     AS column_name,
                               COUNT(*) AS count,
                               os       AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY os
                        
                        UNION ALL
                        
                        SELECT 'location' AS column_name,
                               COUNT(*)   AS count,
                               location   AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY location
                        
                        UNION ALL
                        
                        SELECT 'fabric_api_version' AS column_name,
                               COUNT(*)   AS count,
                               fabric_api_version AS item
                        FROM metrics
                        WHERE project_id IN ($projectId)
                        GROUP BY fabric_api_version;
                        """
            ) { resultSet ->
                while (resultSet.next()) {
                    val columnName = resultSet.getString("column_name")
                    val innerMap = this[columnName] ?: mutableMapOf()

                    if (resultSet.getString("item") != null) innerMap[resultSet.getString("item")] =
                        resultSet.getInt("count")

                    this[columnName] = innerMap
                }
            }
        }

        query("SELECT projects.id, projects.name, projects.owner_id, users.username FROM projects JOIN users ON projects.owner_id = users.id WHERE projects.id IN($projectId) LIMIT 1") { resultSet ->
            while (resultSet.next()) {
                project = Project(
                    resultSet.getInt("id"), resultSet.getString("name"), Project.ProjectOwner(
                        resultSet.getInt("owner_id"), resultSet.getString("username")
                    )
                )
            }
        }

        return ProjectMetric(project ?: return null, metricMap)
    }
}