package dev.syoritohatsuki.fstatsbackend.utils

import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.plugins.json
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Creates an HTTP client with JSON content negotiation and optional Bearer token authentication.
 *
 * @param token Bearer token to authorize the request. If null, the request will be sent unauthenticated.
 * @return Configured [io.ktor.client.HttpClient] instance.
 */
fun ApplicationTestBuilder.jsonClient(token: String? = null) = createClient {
    defaultRequest {
        contentType(ContentType.Application.Json)
        if (token != null) bearerAuth(token)
    }
    install(ContentNegotiation) {
        json(json)
    }
}

/*   Just decoration for debug logs   */
fun <T> List<T>.consoleTable() {

    val headersNames = mapOf("first" to "Key", "second" to "Value")

    if (isNullOrEmpty()) {
        println("List is empty")
        return
    }

    val props = first()!!::class.memberProperties.map { it as KProperty1<T, T?> }

    val headers = props.map { headersNames[it.name] ?: it.name }
    val rows = map { row -> props.map { it.get(row).toString() } }

    val colWidths = headers.indices.map { i -> (listOf(headers) + rows).maxOf { it[i].length } }

    fun formatRow(row: List<String>) = row
        .mapIndexed { i, cell -> cell.padEnd(colWidths[i]) }
        .joinToString(" | ")

    println(formatRow(headers))
    println(colWidths.joinToString("-+-") { "-".repeat(it) })
    rows.forEach { println(formatRow(it)) }
}

fun String.toUser(): User = json.decodeFromString<User>(
    String(
        Base64.getUrlDecoder().decode(
            this.split(".")[1]
        )
    )
)