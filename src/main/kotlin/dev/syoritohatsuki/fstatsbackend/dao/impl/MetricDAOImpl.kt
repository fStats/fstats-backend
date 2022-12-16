package dev.syoritohatsuki.fstatsbackend.dao.impl

import dev.syoritohatsuki.fstatsbackend.dao.MetricDAO
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.metric.*
import dev.syoritohatsuki.fstatsbackend.mics.query
import dev.syoritohatsuki.fstatsbackend.mics.update
import java.sql.Timestamp
import java.time.Instant

object MetricDAOImpl : MetricDAO {
    override fun add(metric: Metric, country: String): Pair<String, Int> {
        var data = Pair("Offline", -1)

        update(
            "INSERT INTO metrics(time, project_id, side, online_mode, minecraft_version, mod_version, os, location) VALUES('${
                Timestamp.from(
                    Instant.now()
                )
            }', '${metric.projectId}', '${metric.side}', '${metric.isOnlineMode ?: "NULL"}', '${metric.minecraftVersion}', '${metric.modVersion}', '${metric.os}', '$country')"
        ) {
            data = Pair("Metric added", it)
        }
        return data
    }

    override fun getAll(): List<Metric> {
        return mutableListOf<Metric>().apply {
            query("SELECT * FROM metrics WHERE side") { resultSet ->
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
            }
        }
    }

    override fun getLastHalfYearById(projectId: Int): List<Metric> {
        return mutableListOf<Metric>().apply {
            query("SELECT * FROM metrics WHERE project_id IN($projectId) AND time > NOW() - INTERVAL '6 months'") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Metric(
                            resultSet.getTimestamp("time").time,
                            resultSet.getInt("project_id"),
                            resultSet.getBoolean("side"),
                            resultSet.getString("minecraft_version"),
                            resultSet.getObject("online_mode") as Boolean?,
                            resultSet.getString("mod_version"),
                            resultSet.getString("os")[0],
                            resultSet.getString("location")
                        )
                    )
                }
            }
        }
    }

    override fun removeByProjectId(projectId: Int): Pair<String, Int> {
        var data: Pair<String, Int> = Pair("Offline", -1)
        update("DELETE FROM exceptions WHERE project_id IN($projectId)") {
            data = Pair("Metric removed", it)
        }
        return data
    }

    override fun getSideById(projectId: Int): List<Side> {
        return mutableListOf<Side>().apply {
            query("SELECT side, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY side") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Side(
                            resultSet.getBoolean("side"),
                            resultSet.getInt("count")
                        )
                    )
                }
            }
        }
    }

    override fun getMcVersionById(projectId: Int): List<MinecraftVersion> {
        return mutableListOf<MinecraftVersion>().apply {
            query("SELECT minecraft_version, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY minecraft_version") { resultSet ->
                while (resultSet.next()) {
                    add(
                        MinecraftVersion(
                            resultSet.getString("minecraft_version"),
                            resultSet.getInt("count")
                        )
                    )
                }
            }
        }
    }

    override fun getOnlineModeById(projectId: Int): List<OnlineMode> {
        return mutableListOf<OnlineMode>().apply {
            query("SELECT online_mode, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY online_mode") { resultSet ->
                while (resultSet.next()) {
                    (resultSet.getObject("online_mode") as Boolean?)?.let {
                        add(
                            OnlineMode(
                                it,
                                resultSet.getInt("count")
                            )
                        )
                    }
                }
            }
        }
    }

    override fun getModVersionById(projectId: Int): List<ModVersion> {
        return mutableListOf<ModVersion>().apply {
            query("SELECT mod_version, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY mod_version") { resultSet ->
                while (resultSet.next()) {
                    add(
                        ModVersion(
                            resultSet.getString("mod_version"),
                            resultSet.getInt("count")
                        )
                    )
                }
            }
        }
    }

    override fun getOsById(projectId: Int): List<OperationSystem> {
        return mutableListOf<OperationSystem>().apply {
            query("SELECT os, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY os") { resultSet ->
                while (resultSet.next()) {
                    add(
                        OperationSystem(
                            resultSet.getString("os")[0],
                            resultSet.getInt("count")
                        )
                    )
                }
            }
        }
    }

    override fun getLocationById(projectId: Int): List<Location> {
        return mutableListOf<Location>().apply {
            query("SELECT location, COUNT(project_id) FROM metrics WHERE project_id IN($projectId) GROUP BY location") { resultSet ->
                while (resultSet.next()) {
                    add(
                        Location(
                            resultSet.getString("location"),
                            resultSet.getInt("count")
                        )
                    )
                }
            }
        }
    }
}