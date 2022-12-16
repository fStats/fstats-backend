package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.mics.getGeolocationByIp
import dev.syoritohatsuki.fstatsbackend.mics.getProjectId
import dev.syoritohatsuki.fstatsbackend.mics.getRemoteIp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.metricsRoute() {
    route("metrics") {
        get {
            call.respond(MetricDAOImpl.getAll())
        }

        route("{id}") {
            get {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                }

                call.respond(MetricDAOImpl.getLastHalfYearById(projectId))
            }

            get("{datatype}") {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                }

                val data: Any = when (call.parameters["datatype"]) {
                    "side" -> MetricDAOImpl.getSideById(projectId)
                    "mcversion" -> MetricDAOImpl.getMcVersionById(projectId)
                    "onlinemode" -> MetricDAOImpl.getOnlineModeById(projectId)
                    "modversion" -> MetricDAOImpl.getModVersionById(projectId)
                    "os" -> MetricDAOImpl.getOsById(projectId)
                    "location" -> MetricDAOImpl.getLocationById(projectId)
                    else -> return@get call.respond(HttpStatusCode.NotFound)
                }

                call.respond(data)
            }

            get("all") {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                }

            }
        }

        post {
            val metric = call.receive<Metric>()

            if (ProjectDAOImpl.getById(metric.projectId) == null) {
                println("${HttpStatusCode.NoContent} Project not found")
                return@post call.respond(HttpStatusCode.NoContent, "Project not found")
            }

            call.getRemoteIp().getGeolocationByIp().let { geoIp ->
                if (geoIp == null || geoIp.status != "success") {
                    println("${HttpStatusCode.BadRequest} Can't resolve location from IP")
                }

                MetricDAOImpl.add(metric, geoIp?.country ?: "Unknown").let {
                    if (it.second == 1) call.respond(HttpStatusCode.Created, "Metric data added") else {
                        println("${HttpStatusCode.BadRequest} ${it.second}")
                        call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                    }
                }
            }

        }
    }
}