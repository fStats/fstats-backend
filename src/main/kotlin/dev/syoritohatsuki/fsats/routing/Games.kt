package dev.syoritohatsuki.fsats.routing

import dev.syoritohatsuki.fsats.dto.StatusMessage
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gamesRoute() {
    route("games") {
        get { call.respond(StatusMessage(message = "List all games")) }
        post("add") { call.respond(StatusMessage(message = "Add new game")) }
        post("remove") { call.respond(StatusMessage(message = "Remove game")) }
    }
}