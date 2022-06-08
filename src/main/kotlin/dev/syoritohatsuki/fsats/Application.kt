package dev.syoritohatsuki.fsats

import dev.syoritohatsuki.fsats.plugins.configureHTTP
import dev.syoritohatsuki.fsats.plugins.configureRouting
import dev.syoritohatsuki.fsats.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureHTTP()
    }.start(wait = true)
}
