package dev.syoritohatsuki.fstatsbackend.containers.debug

import dev.syoritohatsuki.fstatsbackend.containers.Kafka
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object KafkaUI {
    val container: GenericContainer<*> = GenericContainer(DockerImageName.parse("provectuslabs/kafka-ui"))
        .withNetwork(Kafka.network)
        .withExposedPorts(8080)
        .withEnv("DYNAMIC_CONFIG_ENABLED", "true")
        .withEnv("KAFKA_CLUSTERS_0_NAME", "test-cluster")
        .withEnv("KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS", "kafka:9095")
        .dependsOn(Kafka.container)

    init {
        container.start()
    }
}