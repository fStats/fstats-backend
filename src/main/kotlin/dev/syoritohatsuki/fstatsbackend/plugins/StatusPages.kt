package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(StatusPage(status.value, "Page Not Found"))
        }
        exception<Throwable> { call, cause ->
            call.respond(StatusPage(500, cause.localizedMessage))
        }
    }
}

@Serializable
private data class StatusPage(
    val code: Int,
    val message: String
)
