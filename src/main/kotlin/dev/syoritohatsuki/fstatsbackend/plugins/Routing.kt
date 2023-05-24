package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.routing.v1.exceptionsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v1.indexRoute
import dev.syoritohatsuki.fstatsbackend.routing.v1.metricsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v1.projectsRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.authRoute
import dev.syoritohatsuki.fstatsbackend.routing.v2.usersRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import dev.syoritohatsuki.fstatsbackend.routing.v1.usersRoute as deprecatedUsersRoute

fun Application.configureRouting() {
    routing {
        indexRoute()
        route("v1") {
            authRoute()
            exceptionsRoute()
            metricsRoute()
            projectsRoute()
            deprecatedUsersRoute()
        }
        route("v2") {
            usersRoute()
        }
    }
}
