import io.ktor.plugin.features.*

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
}

group = "dev.syoritohatsuki"
version = "2026.1.1"

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
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
}

tasks {
    jar {
        enabled = false
    }

    named("startScripts") {
        dependsOn("shadowJar")
    }
    named("distZip") {
        dependsOn("shadowJar")
    }
    named("distTar") {
        dependsOn("shadowJar")
    }

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

    test {
        useJUnitPlatform {
            excludeEngines("junit-jupiter")
            includeEngines("junit-platform-suite")
        }
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    implementation("de.nycode:bcrypt:2.3.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Ktor dependencies
    val ktorVersion = "3.4.0"
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
    implementation("ch.qos.logback:logback-classic:1.5.25")

    // Database dependencies
    implementation("at.yawk.lz4:lz4-java:1.10.3")
    implementation("com.clickhouse:clickhouse-jdbc:0.9.6")
    implementation("org.postgresql:postgresql:42.7.9")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // Exposed dependencies
    val exposedVersion = "1.0.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    // Broker
    implementation("org.apache.kafka:kafka-clients:4.1.1")

    // Metrics
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.17.0-M1")

    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.3.0")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    testImplementation("org.junit.platform:junit-platform-suite:6.1.0-M1")

    testImplementation("org.testcontainers:testcontainers:2.0.3")
    testImplementation("org.testcontainers:testcontainers-kafka:2.0.1")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.1")
    testImplementation("org.testcontainers:testcontainers-clickhouse:2.0.1")
}
