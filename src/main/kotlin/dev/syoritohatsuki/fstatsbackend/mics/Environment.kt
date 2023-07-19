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

val DISKS_COUNT by environment(1)
val CPU_CORES by environment(Runtime.getRuntime().availableProcessors())

inline fun <reified T : Any> environment(defaultValue: T): ReadOnlyProperty<Any?, T> {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    return ReadOnlyProperty { _, property ->
        val value = dotenv[property.name] ?: System.getenv(property.name)
        when (defaultValue) {
            is String -> value ?: defaultValue
            is Int -> value?.toIntOrNull() ?: defaultValue
            else -> throw IllegalArgumentException("Unsupported property type: ${defaultValue::class.simpleName}")
        } as T
    }
}