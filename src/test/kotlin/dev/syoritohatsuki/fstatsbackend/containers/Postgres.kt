package dev.syoritohatsuki.fstatsbackend.containers

import dev.syoritohatsuki.fstatsbackend.db.FavoritesTable
import dev.syoritohatsuki.fstatsbackend.db.ProjectsTable
import dev.syoritohatsuki.fstatsbackend.db.UsersTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.postgresql.PostgreSQLContainer

object Postgres {

    val container = PostgreSQLContainer("postgres:17-alpine")

    init {
        container.start()

        val (postgresHost, postgresPort, postgresDatabase) = Regex("jdbc:postgresql://([^:/]+):(\\d+)/([^?]+)").find(
            container.jdbcUrl
        )?.destructured ?: throw Exception("Can't destruct Postgres URI")

        System.setProperty("POSTGRES_USER", container.username)
        System.setProperty("POSTGRES_PASS", container.password)
        System.setProperty("POSTGRES_DB", postgresDatabase)
        System.setProperty("POSTGRES_HOST", postgresHost)
        System.setProperty("POSTGRES_PORT", postgresPort)

        transaction(
            Database.connect(
                container.jdbcUrl, user = container.username, password = container.password
            )
        ) {
            SchemaUtils.create(
                UsersTable, ProjectsTable, FavoritesTable
            )
        }
    }
}