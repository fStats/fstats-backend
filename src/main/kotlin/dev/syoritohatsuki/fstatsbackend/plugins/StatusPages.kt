package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, StatusPage(status.value, "Page Not Found"))
        }
        exception<Throwable> { call, cause ->
            val status = HttpStatusCode.InternalServerError
            call.respond(status, StatusPage(status.value, cause.localizedMessage))
            cause.printStackTrace()
        }
    }
}

@Serializable
private data class StatusPage(
    val code: Int,
    val message: String
)
