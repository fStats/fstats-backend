package dev.syoritohatsuki.fstatsbackend.repository.clickhouse

import dev.syoritohatsuki.fstatsbackend.broker.Kafka
import dev.syoritohatsuki.fstatsbackend.dto.MetricLine
import dev.syoritohatsuki.fstatsbackend.dto.MetricPie
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.oldName2ISO
import dev.syoritohatsuki.fstatsbackend.plugins.clickHouseDataSource
import dev.syoritohatsuki.fstatsbackend.plugins.json
import dev.syoritohatsuki.fstatsbackend.repository.MetricRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import java.sql.Types
import java.time.OffsetDateTime

object ClickHouseMetricRepository : MetricRepository {
    override suspend fun add(metrics: Metrics): Int {
        metrics.projectIds.keys.map { projectId ->
            Kafka.publish(
                "fstats-metrics-topic", "$projectId", json.encodeToString(
                    Metrics.Metric(
                        timestampSeconds = OffsetDateTime.now().toEpochSecond(),
                        projectId = projectId,
                        minecraftVersion = metrics.metric.minecraftVersion,
                        isOnlineMode = metrics.metric.isOnlineMode,
                        modVersion = metrics.projectIds[projectId] ?: "unknown",
                        os = metrics.metric.os,
                        location = metrics.metric.location,
                        fabricApiVersion = metrics.metric.fabricApiVersion,
                        isServerSide = metrics.metric.isServerSide
                    )
                )
            )
        }
        return SUCCESS
    }

    override suspend fun getMetricInDateRange(projectId: Int, from: Long?, to: Long, serverSide: Boolean): MetricLine {
        val timestampList = mutableListOf<Long>()
        val countList = mutableListOf<Int>()

        var prevTimestamp: Long = 0
        var prevCount = 0

        @Language("ClickHouse") val sqlQuery = """
            SELECT
                COUNT(*) AS count,
                toUnixTimestamp(timestampz) AS timestampz
            FROM (
                SELECT
                    toStartOfInterval(ts, INTERVAL 30 minute) AS timestampz
                FROM metrics
                WHERE (ts >= coalesce(fromUnixTimestamp(?), ts))
                    AND ts <= fromUnixTimestamp(?)
                    AND project_id = ?
                    AND server_side = ?
            ) AS _
            GROUP BY timestampz
            ORDER BY timestampz;
        """.trimIndent()

        withContext(Dispatchers.IO) {
            clickHouseDataSource.connection.use { connection ->
                connection.prepareStatement(sqlQuery).use { preparedStatement ->
                    if (from != null) {
                        preparedStatement.setLong(1, from)
                    } else {
                        preparedStatement.setNull(1, Types.TIME_WITH_TIMEZONE)
                    }
                    preparedStatement.setLong(2, to)
                    preparedStatement.setInt(3, projectId)
                    preparedStatement.setBoolean(4, serverSide)

                    preparedStatement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val timestamp = resultSet.getLong("timestampz")
                            val count = resultSet.getInt("count")

                            timestampList.add(timestamp - prevTimestamp)
                            countList.add(count - prevCount)

                            prevTimestamp = timestamp
                            prevCount = count
                        }
                    }
                }
            }
        }

        return MetricLine(timestampList, countList)
    }

    override suspend fun getMetricCountById(projectId: Int, serverSide: Boolean): MetricPie {
        val metricPie = mutableMapOf<String, MutableMap<String?, Int>>()

        withContext(Dispatchers.IO) {
            @Language("ClickHouse") val sqlQuery = """
                WITH metrics_data AS (
                    SELECT * FROM metrics
                    WHERE project_id = ?
                        AND ts >= now() - INTERVAL 30 MINUTE
                        AND server_side = ?
                )
                
                SELECT 'minecraft_version' AS metric_type,
                       minecraft_version AS item,
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY minecraft_version
                
                UNION ALL
                
                SELECT 'online_mode' AS metric_type,
                       CAST(online_mode AS TEXT) AS item, 
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY online_mode
                
                UNION ALL
                
                SELECT 'mod_version' AS metric_type,
                       mod_version AS item,
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY mod_version
                
                UNION ALL
                
                SELECT 'os' AS metric_type,
                       os AS item,
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY os
                
                UNION ALL
                
                SELECT 'location' AS metric_type,
                       location AS item,
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY location
                
                UNION ALL
                
                SELECT 'fabric_api_version' AS metric_type,
                       fabric_api_version AS item,
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY fabric_api_version
                
                UNION ALL 
                
                SELECT 'server_side' AS metric_type,
                       CAST(server_side AS TEXT) AS item, 
                       COUNT(*) AS count
                FROM metrics_data
                GROUP BY server_side;
            """.trimIndent()

            clickHouseDataSource.connection.use { connection ->
                connection.prepareStatement(sqlQuery).use { preparedStatement ->
                    preparedStatement.setInt(1, projectId)
                    preparedStatement.setBoolean(2, serverSide)

                    preparedStatement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val columnName = resultSet.getString("metric_type")
                            val innerMap = metricPie[columnName] ?: mutableMapOf()

                            if (resultSet.getString("item") != null) {
                                var item = resultSet.getString("item")
                                if (columnName == "location") item = oldName2ISO[item] ?: item
                                innerMap[item] = resultSet.getInt("count")
                            }

                            metricPie[columnName] = innerMap
                        }
                    }
                }

            }
        }
        return metricPie
    }
}
