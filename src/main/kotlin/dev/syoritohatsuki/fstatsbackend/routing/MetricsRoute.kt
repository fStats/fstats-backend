package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetric
import dev.syoritohatsuki.fstatsbackend.dto.metric.pie.PieMetricList
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
                    call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get
                }
                call.respond(MetricDAOImpl.getLastHalfYearById(projectId))
            }

            get("{datatype}") {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get
                }

                val data: List<PieMetric> = when (call.parameters["datatype"]) {
                    "side" -> MetricDAOImpl.getSideById(projectId)
                    "mcversion" -> MetricDAOImpl.getMcVersionById(projectId)
                    "onlinemode" -> MetricDAOImpl.getOnlineModeById(projectId)
                    "modversion" -> MetricDAOImpl.getModVersionById(projectId)
                    "os" -> MetricDAOImpl.getOsById(projectId)
                    "location" -> MetricDAOImpl.getLocationById(projectId)
                    else -> return@get call.respond(HttpStatusCode.BadGateway)
                }

                call.respond(data)
            }

            get("all") {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@get
                }

                call.respond(
                    PieMetricList(
                        MetricDAOImpl.getSideById(projectId),
                        MetricDAOImpl.getMcVersionById(projectId),
                        MetricDAOImpl.getOnlineModeById(projectId),
                        MetricDAOImpl.getModVersionById(projectId),
                        MetricDAOImpl.getOsById(projectId),
                        MetricDAOImpl.getLocationById(projectId)
                    )
                )
            }
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