package dev.syoritohatsuki.fstatsbackend.mics

import io.github.cdimascio.dotenv.dotenv
import kotlin.reflect.KProperty

class Environment(private val defaultValue: String = "") {
    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    operator fun getValue(th: Any?, prop: KProperty<*>): String = dotenv[prop.name]
        ?: System.getenv(prop.name)
        ?: defaultValue
}

val JWT_SECRET by Environment()
val JWT_REALM by Environment("dev.syoritohatsuki.fstats")

val HOST by Environment("0.0.0.0")
val PORT by Environment("1540")

val POSTGRES_USER by Environment("postgres")
val POSTGRES_PASS by Environment()
val POSTGRES_DB by Environment("fstats")
val POSTGRES_HOST by Environment("localhost")
val POSTGRES_PORT by Environment("5432")