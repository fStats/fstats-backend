package dev.syoritohatsuki.fstatsbackend

import dev.syoritohatsuki.fstatsbackend.mics.HOST
import dev.syoritohatsuki.fstatsbackend.mics.PORT
import dev.syoritohatsuki.fstatsbackend.plugins.configureAuth
import dev.syoritohatsuki.fstatsbackend.plugins.configureCors
import dev.syoritohatsuki.fstatsbackend.plugins.configureRouting
import dev.syoritohatsuki.fstatsbackend.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, host = HOST, port = PORT.toIntOrNull() ?: 1540) {
        configureCors()
        configureRouting()
        configureSerialization()
        configureAuth()
    }.start(wait = true)
}