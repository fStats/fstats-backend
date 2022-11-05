package dev.syoritohatsuki.fstatsbackend

import dev.syoritohatsuki.fstatsbackend.mics.*
import dev.syoritohatsuki.fstatsbackend.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.sql.DriverManager

fun main() {
    embeddedServer(Netty, host = HOST, port = PORT.toIntOrNull() ?: 1540) {
        configureAuth()
        configureCors()
        configureLogging()
        configureRouting()
        configureSerialization()

        /*  Test Area  */
        DriverManager.getConnection(
            "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB",
            POSTGRES_USER,
            POSTGRES_PASS
        ).use { println("Connected: ${it.warnings}") }
    }.start(wait = true)
}