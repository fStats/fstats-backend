package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Project

interface ProjectDAO {
    fun create(project: Project): Pair<String, Int>
    fun deleteById(id: Int): Pair<String, Int>
    fun getAll(): List<Project>
    fun getByOwner(ownerId: Int): List<Project>
    fun getById(id: Int): Project?
}