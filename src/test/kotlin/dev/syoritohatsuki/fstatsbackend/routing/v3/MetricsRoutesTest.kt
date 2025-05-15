package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.*
import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.plugins.clickHouseDataSource
import dev.syoritohatsuki.fstatsbackend.utils.SetupTest
import dev.syoritohatsuki.fstatsbackend.utils.jsonClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Metrics")
@Tags(value = [Tag("v3"), Tag("Metrics")])
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
    @DisplayName("[POST /v3/metrics] - Add metrics for project")
    fun addMetric() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient().post("/v3/metrics") {
            userAgent("fstats")
            setBody(
                Metrics(
                    projectIds = mapOf(
                        projects.first().id to "2025.5.1"
                    ), metric = Metrics.Metric(
                        minecraftVersion = "1.21.5",
                        isOnlineMode = true,
                        os = 'l',
                        location = "XXX",
                        fabricApiVersion = null,
                        isServerSide = true
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Metrics data added", response.body<Message<String>>().message)
    }

    @Test
    @Order(1)
    @DisplayName("[POST /v3/metrics/{projectId}/line] - Get project line metrics")
    fun getLineMetrics() = testApplication {
        application(Application::fStatsModule)

        clickHouseDataSource.connection.use connection@{
            // Waiting while ClickHouse consume data from Kafka and Merge it. In real practice much faster
            while (true) {
                it.prepareStatement("SELECT * FROM metrics").executeQuery().use { result ->
                    if (result.next()) return@connection
                }
                Thread.sleep(100) // Let not DDOS even if that is testing container :)
            }
        }

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient().get("/v3/metrics/${projects.first().id}/line")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(true, response.body<MetricLine>().counts.isNotEmpty())
        assertEquals(true, response.body<MetricLine>().timestamps.isNotEmpty())
    }

    @Test
    @Order(2)
    @DisplayName("[POST /v3/metrics/{projectId}/pie] - Get project pie metrics")
    fun getPieMetrics() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient().get("/v3/metrics/${projects.first().id}/pie")

        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.body<MetricPie>().keys.size > 5)
        assert(response.body<MetricPie>().map { it.value }.isNotEmpty())
    }

    @AfterAll
    fun cleanup() = testApplication {
        application(Application::fStatsModule)
        jsonClient(token).delete("/v3/users")
    }
}