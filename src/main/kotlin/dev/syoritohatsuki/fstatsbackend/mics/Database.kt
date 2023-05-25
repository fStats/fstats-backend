package dev.syoritohatsuki.fstatsbackend.mics

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import kotlin.system.exitProcess

const val SUCCESS = 1

fun connection(): Connection = DriverManager.getConnection(
    "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB",
    POSTGRES_USER,
    POSTGRES_PASS
)

fun checkDatabaseConnection() {
    runCatching {
        connection().use {
            it.close()
        }
    }.onFailure {
        println(it.localizedMessage)
        exitProcess(0)
    }
}

private fun Connection.close(st: Statement, rs: ResultSet) {
    runCatching {
        rs.close()
    }.onFailure { e ->
        println(e.localizedMessage)
    }.onSuccess {
        runCatching {
            st.close()
        }.onFailure { e ->
            println(e.localizedMessage)
        }.onSuccess {
            runCatching {
                close()
            }.onFailure { e ->
                println(e.localizedMessage)
            }
        }
    }
}

fun query(sql: String, resultSet: (ResultSet) -> Unit) {
    runCatching {
        connection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sql).use { resultSet ->
                    resultSet(resultSet)
                    connection.close(statement, resultSet)
                }
            }
        }
    }.onFailure { println(it.localizedMessage) }
}

@Deprecated("Useless shit :) Why I did it?")
fun update(sql: String, code: (Int) -> Unit) {
    runCatching {
        connection().use { connection ->
            connection.createStatement().use { statement ->
                code(statement.executeUpdate(sql))
            }
        }
    }.onFailure { println(it.localizedMessage) }
}

fun update(sql: String): Int {
    runCatching {
        connection().use { connection ->
            connection.createStatement().use { statement ->
                return statement.executeUpdate(sql)
            }
        }
    }.onFailure { println(it.localizedMessage) }
    return -1
}