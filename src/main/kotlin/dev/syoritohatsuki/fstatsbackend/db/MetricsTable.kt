package dev.syoritohatsuki.fstatsbackend.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

object MetricsTable : Table("metrics") {
    val time = timestampWithTimeZone("time")
    val projectId = integer("project_id").references(ProjectsTable.id, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val minecraftVersion = text("minecraft_version")
    val onlineMode = bool("online_mode").nullable()
    val modVersion = text("mod_version")
    val os = char("os")
    val location = text("location")
    val fabricApiVersion = text("fabric_api_version").nullable()
    val serverSide = bool("server_side").nullable().default(true)
}