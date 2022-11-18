package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        indexRoute()
        route("v1") {
            authRoute()
            exceptionsRoute()
            metricsRoute()
            projectsRoute()
            usersRoute()
        }
    }
}
