package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.mics.close
import dev.syoritohatsuki.fstatsbackend.mics.connection
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {
    override fun add(metric: Metric): Pair<String, Int> {
        var sqlRequest = ""
        var sqlParams = ""
        if (metric.isServer) {
            sqlRequest = "online_mode, "
            sqlParams = "${metric.isOnlineMode}, "

        }
        kotlin.runCatching {
            connection().use { connection ->
                connection.createStatement().use { statement ->
                    @Suppress("SqlInsertValues")
                    return Pair(
                        "Metric added", statement.executeUpdate(
                            "INSERT INTO metrics(time, project_id, server, $sqlRequest minecraft_version, mod_version, os, location) VALUES('${
                                Timestamp.from(Instant.now())
                            }', '${metric.projectId}', '${metric.isServer}', $sqlParams '${metric.minecraftVersion}', '${metric.modVersion}', '${metric.os}', '${metric.location}')"
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

    override fun getAll(isMultiplayer: Boolean): List<Metric> {
        return mutableListOf<Metric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM metrics WHERE server IS $isMultiplayer")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        Metric(
                                            resultSet.getTimestamp("time").time,
                                            resultSet.getInt("project_id"),
                                            resultSet.getBoolean("server"),
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

    override fun getLastHalfYearById(projectId: Int, isMultiplayer: Boolean): List<Metric> {
        return mutableListOf<Metric>().apply {
            kotlin.runCatching {
                connection().use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery("SELECT * FROM metrics WHERE project_id IN($projectId) AND server IS $isMultiplayer AND time > NOW() - INTERVAL '6 months'")
                            .use { resultSet ->
                                while (resultSet.next()) {
                                    add(
                                        Metric(
                                            resultSet.getTimestamp("time").time,
                                            resultSet.getInt("project_id"),
                                            resultSet.getBoolean("server"),
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
}