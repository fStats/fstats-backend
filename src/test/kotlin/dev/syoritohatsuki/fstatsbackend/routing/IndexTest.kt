package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.utils.SetupTest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Initialization")
@Tag("Index")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IndexTest : SetupTest() {
    @Test
    @Order(0)
    @DisplayName("[GET /] - Verify index existence")
    fun initialize() = testApplication {
        application(Application::fStatsModule)

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.bodyAsText().isNotBlank())
    }
}