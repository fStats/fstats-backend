package dev.syoritohatsuki.fstatsbackend.mics

import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun ApplicationCall.getRemoteIp(): String = request.headers["X-Real-IP"] ?: request.origin.remoteHost

@Deprecated("Will be moved to Front-end")
fun Char.getOs(): String = when (this) {
    'l' -> "Linux"
    'm' -> "MacOS"
    'w' -> "Windows"
    else -> "Other"
}

@Deprecated("Will be moved to Front-end")
fun Boolean.getSide(): String = when (this) {
    true -> "Server"
    false -> "Client"
}