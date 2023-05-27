package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
    install(RateLimit) {
        global {
            rateLimiter(limit = 300, refillPeriod = 60.seconds)
        }
    }
}