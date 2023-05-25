package dev.syoritohatsuki.fstatsbackend.dao

import dev.syoritohatsuki.fstatsbackend.dto.Project

interface ProjectDAO {
    fun create(name: String, ownerId: Int): Int
    fun deleteById(id: Int): Int
    fun getAll(): List<Project>
    fun getByOwner(ownerId: Int): List<Project>
    fun getById(id: Int): Project?
}