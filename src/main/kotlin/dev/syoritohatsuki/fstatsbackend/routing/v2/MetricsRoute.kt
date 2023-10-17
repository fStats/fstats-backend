package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    route("metrics") {
        route("{id}") {
            get("timeline") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val metrics = MetricDAOImpl.getLastHalfYearById(id)

                call.respond(metrics)
            }
            get("count") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val projectMetric = MetricDAOImpl.getMetricCountById(id) ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Metric data not available"
                )

                call.respond(projectMetric)
            }
        }

        post {
            if (call.request.userAgent()?.contains("fstats") == false)
                return@post call.respondMessage(HttpStatusCode.BadRequest, "Incorrect User-Agent")

            val metrics = call.receive<Metrics>()

            when (MetricDAOImpl.add(metrics)) {
                SUCCESS -> call.respondMessage(HttpStatusCode.Created, "Metrics data added")
                else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}