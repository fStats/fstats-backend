package dev.syoritohatsuki.fstatsbackend.mics

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.intellij.lang.annotations.Language
import java.sql.ResultSet


object Database {
    const val SUCCESS = 1

    private val dataSource: HikariDataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
            username = POSTGRES_USER
            password = POSTGRES_PASS
            maximumPoolSize = CPU_CORES + DISKS_COUNT
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

    fun dataStore(): HikariDataSource = dataSource

    fun query(@Language("PostgreSQL") sql: String, resultSet: (ResultSet) -> Unit) {
        runCatching {
            dataStore().connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeQuery(sql).use { resultSet ->
                        resultSet(resultSet)
                    }
                }
            }
        }.onFailure { println(it.localizedMessage) }
    }

    fun update(@Language("PostgreSQL") sql: String): Int {
        runCatching {
            dataStore().connection.use { connection ->
                connection.createStatement().use { statement ->
                    return statement.executeUpdate(sql)
                }
            }
        }.onFailure { println(it.localizedMessage) }
        return -1
    }
}