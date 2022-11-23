plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.1.3"
}

group = "dev.syoritohatsuki"
version = "2022.10.1"

application {
    mainClass.set("$group.fstatsbackend.ApplicationKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}

ktor {
    fatJar {
        archiveFileName.set("fstats-backend-$version.jar")
    }
}

dependencies {
    val bcryptVersion: String by project
    implementation("de.nycode", "bcrypt", bcryptVersion)

    val dotenvVersion: String by project
    implementation("io.github.cdimascio", "dotenv-kotlin", dotenvVersion)

    val ktorVersion: String by project
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jwt", ktorVersion)

    implementation("io.ktor", "ktor-server-cors", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)

    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)

    implementation("io.ktor", "ktor-server-call-logging-jvm", ktorVersion)

    implementation("io.ktor", "ktor-server-caching-headers", ktorVersion)

    implementation("ch.qos.logback", "logback-classic", "1.4.4")

    implementation("org.postgresql", "postgresql", "42.2.8")

    implementation("dev.forst", "ktor-rate-limiting", "2.1.3")
}