package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.remote.Exception

interface ExceptionDAO {
    fun add(exception: Exception): Pair<String, Int>
    fun getByProject(projectId: Int): List<Exception>
    fun removeByProjectId(projectId: Int): Pair<String, Int>
}