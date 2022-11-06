package dev.syoritohatsuki.fstatsbackend.mics

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

fun connection(): Connection = DriverManager.getConnection(
    "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB",
    POSTGRES_USER,
    POSTGRES_PASS
)

fun Connection.close(st: Statement, rs: ResultSet) {
    try {
        rs.close()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            st.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}