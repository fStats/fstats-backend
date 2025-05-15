package dev.syoritohatsuki.fstatsbackend.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import dev.syoritohatsuki.fstatsbackend.containers.ClickHouse
import dev.syoritohatsuki.fstatsbackend.containers.Kafka
import dev.syoritohatsuki.fstatsbackend.containers.Postgres
import dev.syoritohatsuki.fstatsbackend.containers.debug.KafkaUI
import org.slf4j.LoggerFactory

object SharedEnvironments {
    init {
        // Disable annoying logs
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.ERROR

        // Containers
        Kafka
        KafkaUI
        Postgres
        ClickHouse

        // Require to fix `FATAL: sorry, too many clients already`
        System.setProperty("CPU_CORES", "1")

        // Just my little log just for fun
        System.getProperties().filterKeys {
            it is String && (it.contains("CLICKHOUSE") || it.contains("KAFKA") || it.contains("POSTGRES"))
        }.map { (key, value) ->
            Pair(key, value)
        }.sortedBy { it.first.toString() }.consoleTable()
    }
}