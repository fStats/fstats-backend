package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.mics.getGeolocationByIp
import dev.syoritohatsuki.fstatsbackend.mics.getRemoteIp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    route("metrics") {
        get {
            val isMultiplayer = call.request.queryParameters["multiplayer"]?.toBooleanStrictOrNull() ?: true
            call.respond(MetricDAOImpl.getAll(isMultiplayer))
        }

        get("{id}") {
            val projectId = call.parameters["id"]
            val isMultiplayer = call.request.queryParameters["multiplayer"]?.toBooleanStrictOrNull() ?: true

            if (projectId == null || !Regex("^-?\\d+\$").matches(projectId)) {
                call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                println("${HttpStatusCode.BadRequest} Incorrect project ID")
                return@get
            }
            call.respond(MetricDAOImpl.getLastHalfYearById(projectId.toInt(), isMultiplayer))
        }

        post {
            val metric = call.receive<Metric>()

            if (ProjectDAOImpl.getById(metric.projectId) == null) {
                call.respond(HttpStatusCode.NoContent, "Project not found")
                println("${HttpStatusCode.NoContent} Project not found")
                return@post
            }

            call.getRemoteIp().getGeolocationByIp().let { geoIp ->
                if (geoIp == null || geoIp.status != "success") {
                    call.respond(HttpStatusCode.BadRequest, "Can't resolve location from IP")
                    println("${HttpStatusCode.BadRequest} Can't resolve location from IP")
                }

                MetricDAOImpl.add(metric, geoIp?.country ?: "Unknown").let {
                    if (it.second == 1) call.respond(HttpStatusCode.Created, "Metric data added") else {
                        call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                        println("${HttpStatusCode.BadRequest} ${it.second}")
                    }
                }
            }

        }
    }
}