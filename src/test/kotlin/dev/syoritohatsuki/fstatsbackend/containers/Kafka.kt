package dev.syoritohatsuki.fstatsbackend.containers

import org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.kafka.ConfluentKafkaContainer
import java.util.*

object Kafka {

    const val KAFKA_TOPIC_NAME = "fstats-metrics-topic"

    val network: Network = Network.newNetwork()
    val container: ConfluentKafkaContainer =
        ConfluentKafkaContainer("confluentinc/cp-kafka")
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withListener("kafka:9095")
            .withLogConsumer(Slf4jLogConsumer(LoggerFactory.getLogger(this::class.java)).withPrefix("KAFKA"))


    init {
        container.start()

        (KafkaAdminClient.create(Properties().apply {
            put(BOOTSTRAP_SERVERS_CONFIG, container.bootstrapServers)
        }) as KafkaAdminClient).createTopics(
            mutableListOf(
                NewTopic(KAFKA_TOPIC_NAME, 1, 1)
            )
        )

        val (kafkaHost, kafkaPort) = container.bootstrapServers.split(":")

        System.setProperty("KAFKA_BOOTSTRAP", kafkaHost)
        System.setProperty("KAFKA_PORT", kafkaPort)
    }
}