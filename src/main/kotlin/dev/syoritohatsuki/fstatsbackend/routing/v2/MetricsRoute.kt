package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

@Deprecated("Unused after fStats API 2023.12.3. Will be completely removed after left under 80% of mods that use it")
fun Route.metricsRoute() {
    route("metrics") {
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