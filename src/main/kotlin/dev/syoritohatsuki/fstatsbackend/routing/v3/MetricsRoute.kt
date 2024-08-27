package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import dev.syoritohatsuki.fstatsbackend.repository.postgre.PostgresMetricRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.metricsRoute() {
    route("metrics") {
        route("{id}") {
            get("line") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val from = call.parameters["from"]?.toLongOrNull()
                val to = call.parameters["to"]?.toLongOrNull() ?: Clock.System.now().toEpochMilliseconds()

                val serverSide = call.request.queryParameters["server_side"]?.toBooleanStrictOrNull() ?: true

                call.respond(PostgresMetricRepository.getMetricInDateRange(id, from, to, serverSide))
            }
            get("pie") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val serverSide = call.request.queryParameters["server_side"]?.toBooleanStrictOrNull() ?: true

                call.respond(PostgresMetricRepository.getMetricCountById(id, serverSide))
            }
        }
        post {
            if (call.request.userAgent()?.contains("fstats") == false) return@post call.respondMessage(
                HttpStatusCode.BadRequest, "Incorrect User-Agent"
            )

            val metrics = call.receive<Metrics>()

            when (PostgresMetricRepository.add(metrics)) {
                SUCCESS -> call.respondMessage(HttpStatusCode.Created, "Metrics data added")
                else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}