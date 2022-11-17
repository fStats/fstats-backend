package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    route("metrics") {
        get {
            call.respond(MetricDAOImpl.getAll())
        }
        post {
//            TODO Add new metric data to DB
//            call.receive<Metric>()
        }
        get("{id}") {
//                TODO Get project metric by id in limited time range
//                val interval = call.request.queryParameters["interval"]!!.toInt()
//                val timeunit = TimeUnit.valueOf(call.request.queryParameters["timeunit"].toString())
//                val projectId = call.parameters["id"]
        }
    }
}