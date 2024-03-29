package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.routing.indexRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.authRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.metricsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.projectsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.usersRoute
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        indexRoute()
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        route("v2") {
            authRoute()
            metricsRoute()
            projectsRoute()
            usersRoute()
        }
    }
}