package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Exception

@Deprecated("Dropped in V2 for unknown time")
interface ExceptionDAO {
    fun add(exception: Exception): Pair<String, Int>
    fun getByProject(projectId: Int): List<Exception>
    fun removeByProjectId(projectId: Int): Pair<String, Int>
}