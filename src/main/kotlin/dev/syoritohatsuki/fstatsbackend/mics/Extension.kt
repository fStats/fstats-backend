package dev.syoritohatsuki.fstatsbackend.mics

import dev.syoritohatsuki.fstatsbackend.dto.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend inline fun <reified T> ApplicationCall.respondMessage(httpStatusCode: HttpStatusCode, message: T) {
    respond(httpStatusCode, Message(httpStatusCode.value, message))
}

const val SUCCESS = 1
const val FAILED = 0

suspend fun <T> dbQuery(block: () -> T): T = suspendTransaction { block() }