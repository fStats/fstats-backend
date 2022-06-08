val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val slf4jVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
}

group = "dev.syoritohatsuki"
version = "0.0.1"

application {
    mainClass.set("dev.syoritohatsuki.fstats.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-auth:2.0.2")
    implementation("io.ktor:ktor-server-cors:2.0.2")
    implementation("io.ktor:ktor-server-netty:2.0.2")
    implementation("io.ktor:ktor-server-html-builder:2.0.2")
    implementation("io.ktor:ktor-server-auth-jwt:2.0.2")
    implementation("io.ktor:ktor-server-html-builder:2.0.2")
    implementation("io.ktor:ktor-server-caching-headers:2.0.2")
    implementation("io.ktor:ktor-server-auto-head-response:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")
    implementation("io.ktor:ktor-server-status-pages:2.0.2")

    implementation("org.jetbrains.exposed:exposed-core:0.38.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.38.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.38.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.38.2")

    implementation("mysql:mysql-connector-java:8.0.29")

    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("io.github.cdimascio:java-dotenv:5.2.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
}