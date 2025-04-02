import io.ktor.plugin.features.*

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("io.ktor.plugin") version "3.1.1"
}

group = "dev.syoritohatsuki"
version = "2025.4.1"

application {
    mainClass.set("$group.fstatsbackend.ApplicationKt")
}

repositories {
    mavenCentral()
}

ktor {
    val projectName = project.name
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set(projectName)
        imageTag.set(version.toString())
        portMappings.set(
            listOf(
                DockerPortMapping(
                    1540, 1540, DockerPortMappingProtocol.TCP
                )
            )
        )
        externalRegistry.set(
            DockerImageRegistry.externalRegistry(
                username = provider { System.getProperty("DOCKER_USERNAME") ?: error("DOCKER_USERNAME is undefined") },
                password = provider { System.getProperty("DOCKER_PASSWORD") ?: error("DOCKER_PASSWORD is undefined") },
                project = provider { projectName },
                hostname = provider { System.getProperty("DOCKER_HOSTNAME") ?: error("DOCKER_HOSTNAME is undefined") },
                namespace = provider { "fstats" })
        )
    }
    fatJar {
        archiveFileName.set("$projectName.jar")
    }
}

tasks {
    val cleanBuildDir by registering {
        doLast {
            delete(rootProject.layout.buildDirectory)
        }
    }

    val loadEnv by registering {
        doLast {
            val envFile = file(".env")
            if (envFile.exists()) {
                envFile.readLines()
                    .filter { it.isNotBlank() && !it.startsWith("#") }
                    .forEach { line ->
                        val (key, value) = line.split("=", limit = 2)
                        System.setProperty(key.trim(), value.trim())
                    }
            } else {
                logger.warn(".env file not found!")
            }
        }
    }

    processResources {
        dependsOn("loadEnv")
        filesMatching("index.html") {
            expand("version" to project.version)
        }
    }
}

dependencies {
    implementation("de.nycode:bcrypt:2.3.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Ktor dependencies
    val ktorVersion = "3.1.1"
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    // Logging dependencies
    implementation("ch.qos.logback:logback-classic:1.5.17")

    // Database dependencies
    implementation("com.clickhouse:clickhouse-jdbc:0.8.2")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.zaxxer:HikariCP:6.2.1")

    // Exposed dependencies
    val exposedVersion = "0.60.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    // Broker
    implementation("org.apache.kafka:kafka-clients:3.9.0")
}
