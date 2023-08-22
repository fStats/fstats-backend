package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.dto.ProjectMetric
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.Database.dataStore
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {

    override fun add(metrics: Metrics): Int {
        try {
            dataStore().connection.use { connection ->
                connection.createStatement().execute(
                    "INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location, fabric_api_version) VALUES ${
                        metrics.projectIds.map { (projectId, modVersion) ->
                            "('${Timestamp.from(Instant.now())}', $projectId, ${metrics.metric.isOnlineMode}, '${metrics.metric.minecraftVersion}', '$modVersion', '${metrics.metric.os}', '${metrics.metric.location}', '${metrics.metric.fabricApiVersion}')"
                        }.joinToString(",")
                    }"
                )
                return SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    override fun getLastHalfYearById(projectId: Int): Map<Project, Set<Metric>>? {

        var project: Project? = null

        val metrics = mutableSetOf<Metric>().apply {
            query("SELECT * FROM metrics WHERE project_id IN($projectId) AND time > NOW() - INTERVAL '6 months' ORDER BY time DESC") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Metric(
                            resultSet.getTimestamp("time").time,
                            projectId,
                            resultSet.getString("minecraft_version"),
                            resultSet.getBoolean("online_mode"),
                            resultSet.getString("mod_version"),
                            resultSet.getCharacterStream("os").read().toChar(),
                            resultSet.getString("location"),
                            resultSet.getString("fabric_api_version")
                        )
                    )
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

        return when {
            project == null -> null
            else -> mapOf(project!! to metrics)
        }
    }

    override fun getMetricCountById(projectId: Int): ProjectMetric? {

        var project: Project? = null
        val metricMap = mutableMapOf<String, MutableMap<String, Int>>().apply {
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

                    innerMap[resultSet.getString("item")] = resultSet.getInt("count")
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