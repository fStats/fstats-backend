package dev.syoritohatsuki.fstatsbackend

import dev.syoritohatsuki.fstatsbackend.mics.HOST
import dev.syoritohatsuki.fstatsbackend.mics.PORT
import dev.syoritohatsuki.fstatsbackend.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        host = HOST,
        port = PORT,
        module = Application::fStatsModule
    ).start(wait = true)
}

fun Application.fStatsModule() {
    configureAuth()
    configureCaching()
    configureCompression()
    configureCors()
    configureLogging()
    configureRateLimit()
    configureRouting()
    configureSerialization()
    configureStatusPages()
}