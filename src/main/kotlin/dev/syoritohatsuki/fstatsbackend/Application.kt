package dev.syoritohatsuki.fstatsbackend

import dev.syoritohatsuki.fstatsbackend.mics.HOST
import dev.syoritohatsuki.fstatsbackend.mics.PORT
import dev.syoritohatsuki.fstatsbackend.mics.checkDatabaseConnection
import dev.syoritohatsuki.fstatsbackend.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        host = HOST,
        port = PORT.toIntOrNull() ?: 1540,
        module = Application::fStatsModule
    ).start(wait = true)
}

fun Application.fStatsModule() {
    configureAuth()
    configureCaching()
    configureCors()
    configureLogging()
    configureRateLimiting()
    configureRouting()
    configureSerialization()

    checkDatabaseConnection()
}