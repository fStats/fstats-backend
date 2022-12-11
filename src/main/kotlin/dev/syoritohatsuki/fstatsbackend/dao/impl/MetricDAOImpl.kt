package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetricBoolean
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetricString
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {
    override fun add(metric: Metric, country: String): Pair<String, Int> {
        var sqlRequest = ""
        var sqlParams = ""
        if (metric.side) {
            sqlRequest = "online_mode, "
            sqlParams = "${metric.isOnlineMode}, "

        }
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    @Suppress("SqlInsertValues")
                    return Pair(
                        "Metric added", statement.executeUpdate(
                            "INSERT INTO metrics(time, project_id, side, $sqlRequest minecraft_version, mod_version, os, location) VALUES('${
                                Timestamp.from(Instant.now())
                            }', '${metric.projectId}', '${metric.side}', $sqlParams '${metric.minecraftVersion}', '${metric.modVersion}', '${metric.os}', '$country')"
                        )
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

    override fun getAll(): List<Metric> {
        return mutableListOf<Metric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM metrics WHERE side")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        Metric(
                                            resultSet.getTimestamp("time").time,
                                            resultSet.getInt("project_id"),
                                            resultSet.getBoolean("side"),
                                            resultSet.getString("minecraft_version"),
                                            resultSet.getObject("online_mode") as Boolean?,
                                            resultSet.getString("mod_version"),
                                            resultSet.getCharacterStream("os").read().toChar(),
                                            resultSet.getString("location")
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

    override fun getLastHalfYearById(projectId: Int): List<Metric> {
        return mutableListOf<Metric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM metrics WHERE project_id IN($projectId) AND time > NOW() - INTERVAL '6 months'")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        Metric(
                                            resultSet.getTimestamp("time").time,
                                            resultSet.getInt("project_id"),
                                            resultSet.getBoolean("side"),
                                            resultSet.getString("minecraft_version"),
                                            resultSet.getObject("online_mode") as Boolean?,
                                            resultSet.getString("mod_version"),
                                            resultSet.getCharacterStream("os").read().toChar(),
                                            resultSet.getString("location")
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

    override fun removeByProjectId(projectId: Int): Pair<String, Int> {
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    return Pair(
                        "Metric removed",
                        statement.executeUpdate("DELETE FROM exceptions WHERE project_id IN($projectId)")
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

    override fun getSideById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT side, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY side")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricBoolean(
                                            resultSet.getBoolean("side"),
                                            resultSet.getInt("count")
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

    override fun getMcVersionById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT minecraft_version, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY minecraft_version")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricString(
                                            resultSet.getString("minecraft_version"),
                                            resultSet.getInt("count")
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

    override fun getOnlineModeById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT online_mode, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY online_mode")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricBoolean(
                                            resultSet.getBoolean("online_mode"),
                                            resultSet.getInt("count")
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

    override fun getModVersionById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT mod_version, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY mod_version")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricString(
                                            resultSet.getString("mod_version"),
                                            resultSet.getInt("count")
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

    override fun getOsById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT os, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY os")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricString(
                                            resultSet.getString("os"),
                                            resultSet.getInt("count")
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

    override fun getLocationById(projectId: Int): List<PieMetric> {
        return mutableListOf<PieMetric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT location, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY location")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        PieMetricString(
                                            resultSet.getString("location"),
                                            resultSet.getInt("count")
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