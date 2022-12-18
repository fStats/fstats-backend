package dev.syoritohatsuki.fstatsbackend.mics

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun ApplicationCall.getRemoteIp(): String = request.headers["X-Real-IP"] ?: request.origin.remoteHost

fun Parameters.getProjectId(): Int? = this["id"]?.toIntOrNull().let {
    when (it) {
        is Int -> it
        else -> null
    }
}

fun Char.getOs(): String = when (this) {
    'l' -> "Linux"
    'm' -> "MacOS"
    'w' -> "Windows"
    else -> "Other"
}

fun Boolean.getSide(): String = when (this) {
    true -> "Server"
    false -> "Client"
}