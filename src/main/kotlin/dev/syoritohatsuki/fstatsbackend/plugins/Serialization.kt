package dev.syoritohatsuki.fstatsbackend.plugins

import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetricBoolean
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetricString
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(PieMetric::class) {
            subclass(PieMetricString::class)
            subclass(PieMetricBoolean::class)
        }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(json)
    }
}
