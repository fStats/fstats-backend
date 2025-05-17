package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.Message
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.utils.jsonClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Authorization")
@Tags(value = [Tag("v3"), Tag("Auth")])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthRoutesTest {
    @Test
    @Order(0)
    @DisplayName("[POST /v3/auth/registration] - Registering user")
    fun register() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient().post("/v3/auth/registration") {
            setBody(User(username = "User", password = "Pass1234"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("User created", response.body<Message<String>>().message)
    }

    @Test
    @Order(1)
    @DisplayName("[POST /v3/auth/login] - Login into exist user")
    fun login() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient().post("/v3/auth/login") {
            setBody(User(username = "User", password = "Pass1234"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("token", response.body<Map<String, String>>().firstNotNullOf { it }.key)
    }
}