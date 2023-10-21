package dev.syoritohatsuki.fstatsbackend.mics

import dev.syoritohatsuki.fstatsbackend.dto.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.respondMessage(httpStatusCode: HttpStatusCode, message: T) {
    respond(httpStatusCode, Message(httpStatusCode.value, message))
}
