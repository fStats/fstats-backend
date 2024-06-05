package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.routing.indexRoute
import dev.syoritohatsuki.fstatsbackend.routing.v3.authRoute
import dev.syoritohatsuki.fstatsbackend.routing.v3.metricsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v3.projectsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v3.usersRoute
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import dev.syoritohatsuki.fstatsbackend.routing.v2.metricsRoute as deprecatedMetricsRoute

fun Application.configureRouting() {
    routing {
        indexRoute()
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        route("v2") {
            deprecatedMetricsRoute()
        }
        route("v3") {
            authRoute()
            metricsRoute()
            projectsRoute()
            usersRoute()
        }
    }
}

