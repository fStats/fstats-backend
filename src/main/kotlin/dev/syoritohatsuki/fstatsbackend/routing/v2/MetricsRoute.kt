package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.oldName2ISO
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import dev.syoritohatsuki.fstatsbackend.repository.clickhouse.ClickHouseMetricRepository
import io.ktor.http.*
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

            // Required by v3 database format
            val newLocation = oldName2ISO[metrics.metric.location] ?: "XXX"
            val newMetrics = metrics.copy(metric = metrics.metric.copy(location = newLocation))

            when (ClickHouseMetricRepository.add(newMetrics)) {
                SUCCESS -> call.respondMessage(HttpStatusCode.Created, "Metrics data added")
                else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
            }
        }
    }
}
