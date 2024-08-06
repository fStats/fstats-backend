package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.MetricLine
import dev.syoritohatsuki.fstatsbackend.dto.MetricPie
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.Database.dataStore
import dev.syoritohatsuki.fstatsbackend.mics.Database.query
import dev.syoritohatsuki.fstatsbackend.mics.oldName2ISO
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant

object MetricDAOImpl : MetricDAO {

    override fun add(metrics: Metrics): Int {
        try {
            dataStore().connection.use { connection ->
                connection.prepareStatement("INSERT INTO metrics(time, project_id, online_mode, minecraft_version, mod_version, os, location, fabric_api_version, server_side) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                    .use {
                        metrics.projectIds.forEach { (projectId, modVersion) ->
                            it.setTimestamp(1, Timestamp.from(Instant.now()))
                            it.setInt(2, projectId)
                            if (metrics.metric.isOnlineMode != null) it.setBoolean(
                                3,
                                metrics.metric.isOnlineMode
                            ) else it.setNull(3, Types.BOOLEAN)
                            it.setString(4, metrics.metric.minecraftVersion)
                            it.setString(5, modVersion)
                            it.setString(6, metrics.metric.os.toString())
                            it.setString(7, metrics.metric.location)
                            if (metrics.metric.fabricApiVersion != null) it.setString(
                                8,
                                metrics.metric.fabricApiVersion
                            ) else it.setNull(8, Types.VARCHAR)
                            it.setBoolean(9, metrics.metric.isServerSide)
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

    override fun getMetricInDateRange(projectId: Int, from: Long?, to: Long): MetricLine {

        val timestampList = mutableListOf<Long>()
        val countList = mutableListOf<Int>()

        var prevTimestamp: Long = 0
        var prevCount = 0

        query(
            """
                SELECT
                    COUNT(*)::int AS count,
                    timestampz
                FROM (
                         SELECT
                            EXTRACT(epoch FROM time_bucket('30 minutes', time)) as timestampz
                         FROM metrics
                         WHERE (time >= to_timestamp(${from}) OR $from IS NULL)
                            AND time <= to_timestamp(${to})
                            AND project_id IN(${projectId})
                     ) AS _
                GROUP BY timestampz
                ORDER BY timestampz;
            """
        ) { resultSet ->
            while (resultSet.next()) {
                resultSet.getLong("timestampz").apply {
                    timestampList.add(this - prevTimestamp)
                    prevTimestamp = this
                }

                resultSet.getInt("count").apply {
                    countList.add(this - prevCount)
                    prevCount = this
                }
            }
        }

        return MetricLine(timestampList.toList(), countList.toList())
    }

    override fun getMetricCountById(projectId: Int): MetricPie =
        mutableMapOf<String, MutableMap<String?, Int>>().apply {
            query(
                """
                    SELECT 'minecraft_version' AS column_name,
                           COUNT(*)            AS count,
                           minecraft_version   AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY minecraft_version
                    
                    UNION ALL
                    
                    SELECT 'online_mode'             AS column_name,
                           COUNT(*)                  AS count,
                           CAST(online_mode AS TEXT) AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY online_mode
                    
                    UNION ALL
                    
                    SELECT 'mod_version' AS column_name,
                           COUNT(*)      AS count,
                           mod_version   AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY mod_version
                    
                    UNION ALL
                    
                    SELECT 'os'     AS column_name,
                           COUNT(*) AS count,
                           os       AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY os
                    
                    UNION ALL
                    
                    SELECT 'location' AS column_name,
                           COUNT(*)   AS count,
                           location   AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY location
                    
                    UNION ALL
                    
                    SELECT 'fabric_api_version' AS column_name,
                           COUNT(*)             AS count,
                           fabric_api_version   AS item
                    FROM metrics
                    WHERE project_id IN ($projectId)
                        AND time >= NOW() - INTERVAL '30 minutes'
                    GROUP BY fabric_api_version;
                        """
            ) { resultSet ->
                while (resultSet.next()) {
                    val columnName = resultSet.getString("column_name")
                    val innerMap = this[columnName] ?: mutableMapOf()

                    if (resultSet.getString("item") != null) {
                        var item = resultSet.getString("item")
                        if (columnName == "location") item = oldName2ISO[item] ?: "XXX"
                        innerMap[item] = resultSet.getInt("count")
                    }

                    this[columnName] = innerMap
                }
            }
        }
}