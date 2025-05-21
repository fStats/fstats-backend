package dev.syoritohatsuki.fstatsbackend.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.configureMetrics() {
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
}