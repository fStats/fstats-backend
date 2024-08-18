package dev.syoritohatsuki.fstatsbackend.repository.postgre

import dev.syoritohatsuki.fstatsbackend.db.MetricsTable
import dev.syoritohatsuki.fstatsbackend.dto.MetricLine
import dev.syoritohatsuki.fstatsbackend.dto.MetricPie
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.dbQuery
import dev.syoritohatsuki.fstatsbackend.mics.oldName2ISO
import dev.syoritohatsuki.fstatsbackend.repository.MetricRepository
import kotlinx.coroutines.Dispatchers
import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.OffsetDateTime

object PostgresMetricRepository : MetricRepository {
    override suspend fun add(metrics: Metrics): Int = dbQuery {
        MetricsTable.batchInsert(metrics.projectIds.keys) { projectId ->
            this[MetricsTable.time] = OffsetDateTime.now()
            this[MetricsTable.projectId] = projectId
            this[MetricsTable.onlineMode] = metrics.metric.isOnlineMode
            this[MetricsTable.minecraftVersion] = metrics.metric.minecraftVersion
            this[MetricsTable.modVersion] = metrics.metric.modVersion
            this[MetricsTable.os] = metrics.metric.os
            this[MetricsTable.location] = metrics.metric.location
            this[MetricsTable.fabricApiVersion] = metrics.metric.fabricApiVersion
            this[MetricsTable.serverSide] = metrics.metric.isServerSide
        }
        SUCCESS
    }

    override suspend fun getMetricInDateRange(projectId: Int, from: Long?, to: Long): MetricLine {
        val timestampList = mutableListOf<Long>()
        val countList = mutableListOf<Int>()

        var prevTimestamp: Long = 0
        var prevCount = 0

        @Language("PostgreSQL") val sql = """
            SELECT
                COUNT(*)::int AS count,
                timestampz
            FROM (
                     SELECT
                        EXTRACT(epoch FROM time_bucket('30 minutes', time)) as timestampz
                     FROM metrics
                     WHERE (time >= COALESCE(to_timestamp(?), time))
                        AND time <= to_timestamp(?)
                        AND project_id = ?
                 ) AS _
            GROUP BY timestampz
            ORDER BY timestampz;
        """

        newSuspendedTransaction(Dispatchers.IO) {
            val resultSet = connection.prepareStatement(sql, true).apply {
                from?.let { this[1] = it } ?: this.setNull(1, MetricsTable.time.columnType)

                this[2] = to
                this[3] = projectId
            }.executeQuery()

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

        return MetricLine(timestampList, countList)
    }

    override suspend fun getMetricCountById(projectId: Int): MetricPie {
        val metricPie = mutableMapOf<String, MutableMap<String?, Int>>()

        newSuspendedTransaction(Dispatchers.IO) {
            val sqlQuery = """
            SELECT 'minecraft_version' AS column_name,
                   COUNT(*)            AS count,
                   minecraft_version   AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY minecraft_version
            
            UNION ALL
            
            SELECT 'online_mode'             AS column_name,
                   COUNT(*)                  AS count,
                   CAST(online_mode AS TEXT) AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY online_mode
            
            UNION ALL
            
            SELECT 'mod_version' AS column_name,
                   COUNT(*)      AS count,
                   mod_version   AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY mod_version
            
            UNION ALL
            
            SELECT 'os'     AS column_name,
                   COUNT(*) AS count,
                   os       AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY os
            
            UNION ALL
            
            SELECT 'location' AS column_name,
                   COUNT(*)   AS count,
                   location   AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY location
            
            UNION ALL
            
            SELECT 'fabric_api_version' AS column_name,
                   COUNT(*)             AS count,
                   fabric_api_version   AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY fabric_api_version 
            
            UNION ALL
            
            SELECT 'server_side' AS column_name,
                   COUNT(*)      AS count,
                   CAST(server_side AS TEXT)   AS item
            FROM metrics
            WHERE project_id = ?
                AND time >= NOW() - INTERVAL '30 minutes'
            GROUP BY server_side;
        """.trimIndent()

            val resultSet = connection.prepareStatement(sqlQuery, true).apply {
                set(1, projectId)
                set(2, projectId)
                set(3, projectId)
                set(4, projectId)
                set(5, projectId)
                set(6, projectId)
                set(7, projectId)
            }.executeQuery()

            while (resultSet.next()) {
                val columnName = resultSet.getString("column_name")
                val innerMap = metricPie[columnName] ?: mutableMapOf()

                if (resultSet.getString("item") != null) {
                    var item = resultSet.getString("item")
                    if (columnName == "location") item = oldName2ISO[item] ?: "XXX"
                    innerMap[item] = resultSet.getInt("count")
                }

                metricPie[columnName] = innerMap
            }
        }

        return metricPie
    }
}