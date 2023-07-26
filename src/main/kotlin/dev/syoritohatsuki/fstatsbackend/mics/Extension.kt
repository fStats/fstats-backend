package dev.syoritohatsuki.fstatsbackend.mics

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

suspend inline fun <reified T> ApplicationCall.respondMessage(httpStatusCode: HttpStatusCode, message: T) {
    respond(httpStatusCode, Message(httpStatusCode.value, message))
}

@Serializable
data class Message<T>(
    val code: Int,
    val message: T
)