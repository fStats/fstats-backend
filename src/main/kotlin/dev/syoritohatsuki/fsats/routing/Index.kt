package dev.syoritohatsuki.fsats.routing

import dev.syoritohatsuki.fsats.dto.StatusMessage
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.indexRoute() {
    get { call.respond(StatusMessage()) }
}