package dev.syoritohatsuki.fsats.plugins

import dev.syoritohatsuki.fsats.dto.StatusMessage
import dev.syoritohatsuki.fsats.routing.gamesRoute
import dev.syoritohatsuki.fsats.routing.indexRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(StatusPages) {
        HttpStatusCode.allStatusCodes.forEach {
            status(it) { call, status ->
                call.respond(
                    status = status,
                    message = StatusMessage(
                        code = status.value,
                        message = status.description
                    )
                )
            }
        }
    }

    routing {
        indexRoute()
        gamesRoute()
    }
}
