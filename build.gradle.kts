plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.3.4"
}

group = "dev.syoritohatsuki"
version = "2023.10.2"

application {
    mainClass.set("$group.fstatsbackend.ApplicationKt")
}

repositories {
    mavenCentral()
}

ktor {
    fatJar {
        archiveFileName.set("fstats-backend.jar")
    }
}

tasks {
    delete(rootProject.buildDir)
    processResources {
        filesMatching("index.html") {
            expand(mutableMapOf("version" to project.version))
        }
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
    implementation("io.ktor", "ktor-server-compression-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-rate-limit", ktorVersion)
    implementation("io.ktor", "ktor-server-status-pages", ktorVersion)

    implementation("ch.qos.logback", "logback-classic", "1.4.4")

    implementation("org.postgresql", "postgresql", "42.2.8")

    implementation("com.zaxxer", "HikariCP", "5.0.1")
}