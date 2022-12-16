package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.metric.*

interface MetricDAO {
    fun add(metric: Metric, country: String): Pair<String, Int>
    fun getAll(): List<Metric>
    fun getLastHalfYearById(projectId: Int): List<Metric>
    fun removeByProjectId(projectId: Int): Pair<String, Int>


    fun getSideById(projectId: Int): List<Side>
    fun getMcVersionById(projectId: Int): List<MinecraftVersion>
    fun getOnlineModeById(projectId: Int): List<OnlineMode>
    fun getModVersionById(projectId: Int): List<ModVersion>
    fun getOsById(projectId: Int): List<OperationSystem>
    fun getLocationById(projectId: Int): List<Location>
}