package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

fun Route.metricsRoute() {
    route("metrics") {
        route("{id}") {
            get("line") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val from = call.parameters["from"]?.toLongOrNull()
                val to = call.parameters["to"]?.toLongOrNull() ?: Instant.now().toEpochMilli()

                call.respond(MetricDAOImpl.getMetricInDateRange(id, from, to))
            }
            get("pie") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val projectMetric = MetricDAOImpl.getMetricCountById(id)

                call.respond(projectMetric)
            }
        }
        post {
            if (call.request.userAgent()?.contains("fstats") == false) return@post call.respondMessage(
                HttpStatusCode.BadRequest, "Incorrect User-Agent"
            )

            val metrics = call.receive<Metrics>()

            when (MetricDAOImpl.add(metrics)) {
                SUCCESS -> call.respondMessage(HttpStatusCode.Created, "Metrics data added")
                else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}