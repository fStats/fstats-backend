package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun Route.metricsRoute() {
    route("metrics") {
        route("{id}") {
            get("timeline") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val from = call.parameters["from"]?.toLongOrNull()
                val to = call.parameters["to"]?.toLongOrNull() ?: Instant.now().toEpochMilli()

                call.respond(MetricDAOImpl.getMetricInDateRange(id, from, to))
            }
        }
    }
}