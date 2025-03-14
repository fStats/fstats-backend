package dev.syoritohatsuki.fstatsbackend.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.syoritohatsuki.fstatsbackend.mics.*
import org.jetbrains.exposed.sql.Database

lateinit var postgresDatabase: Database
lateinit var clickHouseDataSource: HikariDataSource

fun configureDatabase() {
    postgresDatabase = Database.connect(HikariDataSource(HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
        username = POSTGRES_USER
        password = POSTGRES_PASS
        maximumPoolSize = CPU_CORES + DISKS_COUNT
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }))

    clickHouseDataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = "com.clickhouse.jdbc.Driver"
        jdbcUrl = "jdbc:clickhouse://$CLICKHOUSE_HOST:$CLICKHOUSE_PORT/$CLICKHOUSE_DB"
        username = CLICKHOUSE_USER
        password = CLICKHOUSE_PASS
        maximumPoolSize = CPU_CORES + DISKS_COUNT
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        addDataSourceProperty("jdbc_ignore_unsupported_values", true)
        validate()
    })
}
