package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json)
    }
}
