plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "dev.syoritohatsuki"
version = "2022.10.1"

repositories {
    mavenCentral()
}

dependencies {
    val dotenvVersion: String by project
    implementation("io.github.cdimascio", "dotenv-kotlin", dotenvVersion)

    val ktorVersion: String by project
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jwt", ktorVersion)

    implementation("io.ktor", "ktor-server-cors", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)

    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
}