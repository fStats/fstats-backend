package dev.syoritohatsuki.fstatsbackend.mics

import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun ApplicationCall.getRemoteIp(): String = request.headers["X-Real-IP"] ?: request.origin.remoteHost