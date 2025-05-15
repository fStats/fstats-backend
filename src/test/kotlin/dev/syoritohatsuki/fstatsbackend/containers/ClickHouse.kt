package dev.syoritohatsuki.fstatsbackend.containers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.syoritohatsuki.fstatsbackend.mics.CPU_CORES
import dev.syoritohatsuki.fstatsbackend.mics.DISKS_COUNT
import org.intellij.lang.annotations.Language
import org.testcontainers.clickhouse.ClickHouseContainer

object ClickHouse {

    val container: ClickHouseContainer = ClickHouseContainer("clickhouse/clickhouse-server:25.3.2.39")
        .withNetwork(Kafka.network)
        .dependsOn(Kafka.container)

    init {
        container.start()

        val (clickHouseHost, clickHousePort, clickHouseDatabase) = Regex("jdbc:clickhouse://([^:/]+):(\\d+)/([^?]+)").find(
            container.jdbcUrl
        )?.destructured ?: throw Exception("Can't destruct ClickHouse URI")

        System.setProperty("CLICKHOUSE_USER", container.username)
        System.setProperty("CLICKHOUSE_PASS", container.password)
        System.setProperty("CLICKHOUSE_DB", clickHouseDatabase)
        System.setProperty("CLICKHOUSE_HOST", clickHouseHost)
        System.setProperty("CLICKHOUSE_PORT", clickHousePort)

        HikariDataSource(HikariConfig().apply {
            driverClassName = "com.clickhouse.jdbc.Driver"
            jdbcUrl = container.jdbcUrl
            username = container.username
            password = container.password
            maximumPoolSize = CPU_CORES + DISKS_COUNT
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            addDataSourceProperty("jdbc_ignore_unsupported_values", true)
            validate()
        }).connection.use { conn ->
            @Language("ClickHouse") val createMetricsQuery = """
                CREATE TABLE metrics
                (
                    ts                 DateTime,
                    project_id         UInt64,
                    minecraft_version  String,
                    online_mode        Nullable(Bool),
                    mod_version        String,
                    os                 String,
                    location           String,
                    fabric_api_version Nullable(String),
                    server_side        Bool
                )
                    ENGINE = MergeTree
                        PARTITION BY toYYYYMM(ts)
                        ORDER BY (toYYYYMM(ts), project_id, ts)
                        TTL toDateTime(ts) + toIntervalYear(4)
                        SETTINGS index_granularity = 8192;
                """.trimIndent()

            @Language("ClickHouse") val createMetricsQueueQuery = """
                CREATE TABLE metrics_queue
                (
                    timestampSeconds     UInt64,
                    projectId            UInt64,
                    minecraftVersion     String,
                    isOnlineMode         Nullable(Bool),
                    modVersion           String,
                    os                   String,
                    location             String,
                    fabricApiVersion     Nullable(String),
                    isServerSide         Nullable(Bool)
                )
                    ENGINE = Kafka
                        SETTINGS
                            kafka_broker_list = 'kafka:9095',
                            kafka_topic_list = '${Kafka.KAFKA_TOPIC_NAME}',
                            kafka_group_name = 'fstats-metrics-clickhouse',
                            kafka_format = 'JSONEachRow',
                            kafka_num_consumers = 1,
                            kafka_thread_per_consumer = 0;
            """.trimIndent()

            @Language("ClickHouse") val createMaterializeMetricsQuery = """
                CREATE MATERIALIZED VIEW metrics_mv TO metrics (
                    `ts` DateTime,
                    `project_id` UInt64,
                    `minecraft_version` String,
                    `online_mode` Nullable(Bool),
                    `mod_version` String,
                    `os` String,
                    `location` String,
                    `fabric_api_version` Nullable(String),
                    `server_side` Nullable(UInt8)
                ) AS SELECT toDateTime(timestampSeconds)        AS ts,
                    projectId                                   AS project_id,
                    minecraftVersion                            AS minecraft_version,
                    isOnlineMode                                AS online_mode,
                    modVersion                                  AS mod_version,
                    os,
                    location,
                    fabricApiVersion                            AS fabric_api_version,
                    if(isServerSide IS NULL, 1, isServerSide)   AS server_side
                FROM metrics_queue;
            """.trimIndent()

            conn.createStatement().apply {
                execute(createMetricsQuery)
                execute(createMetricsQueueQuery)
                execute(createMaterializeMetricsQuery)
            }
        }
    }
}