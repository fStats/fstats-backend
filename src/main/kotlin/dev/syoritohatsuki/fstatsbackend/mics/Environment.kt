package dev.syoritohatsuki.fstatsbackend.mics

import io.github.cdimascio.dotenv.dotenv
import kotlin.properties.ReadOnlyProperty

val JWT_SECRET by environment("")
val JWT_REALM by environment("dev.syoritohatsuki.fstats")

val HOST by environment("0.0.0.0")
val PORT by environment(1540)

val POSTGRES_USER by environment("postgres")
val POSTGRES_PASS by environment("")
val POSTGRES_DB by environment("fstats")
val POSTGRES_HOST by environment("localhost")
val POSTGRES_PORT by environment(5432)

val CLICKHOUSE_USER by environment("fstats")
val CLICKHOUSE_PASS by environment("")
val CLICKHOUSE_DB by environment("fstats")
val CLICKHOUSE_HOST by environment("localhost")
val CLICKHOUSE_PORT by environment(8123)

val KAFKA_BOOTSTRAP by environment("localhost")
val KAFKA_PORT by environment(9092)

val DISKS_COUNT by environment(1)
val CPU_CORES by environment(Runtime.getRuntime().availableProcessors())

inline fun <reified T> environment(defaultValue: T): ReadOnlyProperty<T?, T> = ReadOnlyProperty { _, property ->
    val envValue = System.getProperty(property.name) ?: dotenv { ignoreIfMissing = true }[property.name]
    ?: System.getenv(property.name)

    when (T::class) {
        String::class -> envValue ?: defaultValue
        Int::class -> envValue?.toIntOrNull() ?: defaultValue
        Long::class -> envValue?.toLongOrNull() ?: defaultValue
        Boolean::class -> envValue?.toBooleanStrictOrNull() ?: defaultValue
        Double::class -> envValue?.toDoubleOrNull() ?: defaultValue
        Float::class -> envValue?.toFloatOrNull() ?: defaultValue
        else -> defaultValue
    } as T
}
