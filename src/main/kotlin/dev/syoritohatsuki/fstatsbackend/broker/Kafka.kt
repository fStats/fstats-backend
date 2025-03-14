package dev.syoritohatsuki.fstatsbackend.broker

import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.mics.KAFKA_BOOTSTRAP
import dev.syoritohatsuki.fstatsbackend.mics.KAFKA_PORT
import dev.syoritohatsuki.fstatsbackend.plugins.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.OffsetDateTime
import java.util.*


object Kafka {

    private var metricsProducer: Producer<Int, String> = KafkaProducer(Properties().apply {
        this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "${KAFKA_BOOTSTRAP}:${KAFKA_PORT}"
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java.name
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        this[ProducerConfig.ACKS_CONFIG] = "all"
    })

    fun publish(metrics: Metrics) {
        CoroutineScope(Dispatchers.IO).launch {
            println("New metrics")
            try {
                metrics.projectIds.keys.map { projectId ->
                    metricsProducer.send(
                        ProducerRecord(
                            "fstats-metrics-topic",
                            projectId,
                            json.encodeToString(
                                Metrics.Metric(
                                    timestampSeconds = OffsetDateTime.now().toEpochSecond(),
                                    projectId = projectId,
                                    minecraftVersion = metrics.metric.minecraftVersion,
                                    isOnlineMode = metrics.metric.isOnlineMode,
                                    modVersion = metrics.projectIds[projectId] ?: "unknown",
                                    os = metrics.metric.os,
                                    location = metrics.metric.location,
                                    fabricApiVersion = metrics.metric.fabricApiVersion,
                                    isServerSide = metrics.metric.isServerSide
                                )
                            )
                        )
                    ) { metadata, exception ->
                        if (exception != null) {
                            System.err.println("Error sending record: " + exception.message)
                            exception.printStackTrace()
                        } else {
                            println("Record sent successfully to topic ${metadata.topic()} partition ${metadata.partition()} at offset ${metadata.offset()}")
                        }
                    }
                }
            } catch (e: Exception) {
                System.err.println("Error creating or sending producer: " + e.message)
                metricsProducer.close()
                e.printStackTrace()
            }
        }
    }
}
