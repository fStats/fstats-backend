package dev.syoritohatsuki.fstatsbackend.broker

import dev.syoritohatsuki.fstatsbackend.mics.KAFKA_BOOTSTRAP
import dev.syoritohatsuki.fstatsbackend.mics.KAFKA_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object Kafka {
    private val metricsProducer: Producer<String, String> by lazy {
        KafkaProducer(Properties().apply {
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "$KAFKA_BOOTSTRAP:$KAFKA_PORT"
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name
            ProducerConfig.ACKS_CONFIG to "all"
        })
    }

    fun publish(topicName: String, key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                metricsProducer.send(ProducerRecord(topicName, key, value)) { metadata, exception ->
                    exception?.let {
                        System.err.println("Error sending record to ${metadata.topic()}: ${it.message}")
                        it.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                System.err.println("Error creating or sending producer: ${e.message}")
                metricsProducer.close()
                e.printStackTrace()
            }
        }
    }
}
