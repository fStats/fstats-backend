package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dto.Message
import dev.syoritohatsuki.fstatsbackend.dto.Metrics
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.utils.SetupTest
import dev.syoritohatsuki.fstatsbackend.utils.jsonClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Metrics (Deprecated)")
@Tags(value = [Tag("v2"), Tag("Deprecated"), Tag("Metrics")])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MetricsRoutesTest : SetupTest() {

    private lateinit var token: String

    @BeforeAll
    fun setup() = testApplication {
        application(Application::fStatsModule)

        jsonClient().post("/v3/auth/registration") {
            setBody(User(username = "MetricsTest", password = "Qwerty1234"))
        }

        jsonClient().post("/v3/auth/login") {
            setBody(User(username = "MetricsTest", password = "Qwerty1234"))
        }.body<Map<String, String>>().let {
            token = it["token"] ?: throw AssertionError("Can't login to get token for projects tests")
        }

        jsonClient(token).post("/v3/projects") {
            setBody(Project(name = "Metrics Project"))
        }

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()
        jsonClient(token).post("/v3/projects/${projects.first().id}/favorite")
    }

    @Test
    @Order(0)
    @DisplayName("[POST /v2/metrics] - Add metrics for project")
    fun addMetric() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient().post("/v2/metrics") {
            userAgent("fstats")
            setBody(
                Metrics(
                    projectIds = mapOf(
                        projects.first().id to "2025.5.1"
                    ), metric = Metrics.Metric(
                        minecraftVersion = "1.21.5",
                        isOnlineMode = true,
                        os = 'l',
                        location = "Finland",
                        fabricApiVersion = null
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Metrics data added", response.body<Message<String>>().message)
    }

    @AfterAll
    fun cleanup() = testApplication {
        application(Application::fStatsModule)
        jsonClient(token).delete("/v3/users")
    }
}