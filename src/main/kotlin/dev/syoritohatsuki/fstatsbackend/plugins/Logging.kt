package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val remoteHost = call.request.origin.remoteHost
            val path = call.request.path()
            "$remoteHost Agent[ $userAgent ] ($httpMethod)[$status] $path"
        }
    }
}