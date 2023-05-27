package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    route("metrics") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                call.respond(MetricDAOImpl.getLastHalfYearById(id))
            }
        }

        post {
            if (call.request.userAgent()?.contains("fstats") == false)
                return@post call.respond(HttpStatusCode.BadRequest)

            val metrics = call.receive<Metrics>()

            when (MetricDAOImpl.add(metrics)) {
                SUCCESS -> call.respond(HttpStatusCode.Created, "Metrics data added")
                else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}