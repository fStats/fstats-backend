plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.ktor.plugin") version "2.3.11"
}

group = "dev.syoritohatsuki"
version = "2024.1.1"

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
    delete(rootProject.layout.buildDirectory)
    processResources {
        filesMatching("index.html") {
            expand(mutableMapOf("version" to project.version))
        }
    }
}

dependencies {
    implementation("de.nycode:bcrypt:2.3.0")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")

    implementation("io.ktor:ktor-server-auth:2.3.11")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.11")

    implementation("io.ktor:ktor-server-cors:2.3.11")
    implementation("io.ktor:ktor-server-netty:2.3.11")

    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")

    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.11")

    implementation("io.ktor:ktor-server-caching-headers:2.3.11")
    implementation("io.ktor:ktor-server-compression-jvm:2.3.11")
    implementation("io.ktor:ktor-server-rate-limit:2.3.11")
    implementation("io.ktor:ktor-server-status-pages:2.3.11")

    implementation("io.ktor:ktor-server-swagger:2.3.11")

    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("org.postgresql:postgresql:42.7.2")

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.53.0")

}