package dev.syoritohatsuki.fstatsbackend.plugins

import dev.forst.ktor.ratelimiting.RateLimiting
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import java.time.Duration

fun Application.configureRateLimiting() {
    install(RateLimiting) {
        registerLimit(
            limit = 10,
            window = Duration.ofMinutes(1)
        ) {
            request.origin.host
        }
        excludeRequestWhen {
            request.path().endsWith("metrics")
            request.path().endsWith("exceptions")
        }
    }
}