package dev.syoritohatsuki.fstatsbackend.routing.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.indexRoute() {
    get {
        call.respond(mutableMapOf("page" to "/"))
    }
}