package dev.syoritohatsuki.fstatsbackend.broker

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.plugins.json
import io.nats.client.Connection
import io.nats.client.JetStream
import io.nats.client.Nats
import io.nats.client.impl.Headers
import io.nats.client.impl.NatsMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import java.nio.charset.StandardCharsets

object Nats {

    private val connection: Connection = Nats.connect("nats://localhost:4222")
    private val jetStream: JetStream = connection.jetStream()

    private val retryQueue = Channel<Metrics.Metric>(capacity = Channel.UNLIMITED)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) try {
                processRetryQueue()
            } catch (e: Exception) {
                println("❌ Retry processor failed: ${e.message}")
                delay(2000)
            }
        }
    }

    suspend fun publish(metric: Metrics.Metric) {
        val msg = NatsMessage.builder().headers(Headers().apply {
            add("Content-Type", "application/json")
            add("fStats-Backend-Version", "2025.2.1")
        }).subject(metric.getSubject()).data(json.encodeToString(metric), StandardCharsets.UTF_8).build()

        val future = jetStream.publishAsync(msg)

        try {
            val ack = withTimeout(5_000) { future.get() }
            println("✅ Published: ${ack.stream} / ${ack.seqno}")
        } catch (e: Exception) {
            println("⚠️ Failed to publish ${metric.projectId}, retrying...")
            retryQueue.send(metric)
        }
    }

    private suspend fun processRetryQueue() {
        for (msg in retryQueue) {
            delay(1000)
            println("♻️ Retrying message: ${msg.projectId}")
            publish(msg)
        }
    }
}