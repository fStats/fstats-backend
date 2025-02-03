import io.ktor.plugin.features.*

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.ktor.plugin") version "2.3.12"
}

group = "dev.syoritohatsuki"
version = "2025.2.1"

application {
    mainClass.set("$group.fstatsbackend.ApplicationKt")
}

repositories {
    mavenCentral()
}

ktor {
    val projectName = project.name
    docker {
        jreVersion.set(JavaVersion.VERSION_17)
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
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")

    // Ktor dependencies
    val ktorVersion = "2.3.11"
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

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.7")

    // Database
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.53.0")
}