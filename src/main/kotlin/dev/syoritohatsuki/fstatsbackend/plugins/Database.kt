package dev.syoritohatsuki.fstatsbackend.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.syoritohatsuki.fstatsbackend.mics.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    Database.connect(HikariDataSource(HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
        username = POSTGRES_USER
        password = POSTGRES_PASS
        maximumPoolSize = CPU_CORES + DISKS_COUNT
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }))
}