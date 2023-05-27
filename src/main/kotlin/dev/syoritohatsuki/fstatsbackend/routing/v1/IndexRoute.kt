package dev.syoritohatsuki.fstatsbackend.routing.v1

import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.indexRoute() {
    staticResources("/", "")
}