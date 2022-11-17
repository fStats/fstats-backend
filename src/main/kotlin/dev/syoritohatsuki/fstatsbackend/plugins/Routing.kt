package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.routing.authRoute
import dev.syoritohatsuki.fstatsbackend.routing.indexRoute
import dev.syoritohatsuki.fstatsbackend.routing.usersRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        indexRoute()
        route("v1") {
            authRoute()
            usersRoute()
        }
    }
}
