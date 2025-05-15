package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.Message
import dev.syoritohatsuki.fstatsbackend.dto.Project
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

@DisplayName("Projects")
@Tags(value = [Tag("v3"), Tag("Projects")])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProjectsRoutesTest {

    private lateinit var token: String

    @BeforeAll
    fun setup() = testApplication {
        application(Application::fStatsModule)

        jsonClient().post("/v3/auth/registration") {
            setBody(User(username = "ProjectTest", password = "Qwerty1234"))
        }

        jsonClient().post("/v3/auth/login") {
            setBody(User(username = "ProjectTest", password = "Qwerty1234"))
        }.body<Map<String, String>>().let {
            token = it["token"] ?: throw AssertionError("Can't login to get token for projects tests")
        }
    }

    @Test
    @Order(0)
    @DisplayName("[POST /v3/projects] - Creating project")
    fun createProject() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient(token).post("/v3/projects") {
            setBody(Project(name = "Test Project"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Project created", response.body<Message<String>>().message)
    }

    @Test
    @Order(1)
    @DisplayName("[GET /v3/projects] - List projects")
    fun listProjects() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient().get("/v3/projects")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Test Project", response.body<List<Project>>().first().name)
    }

    @Test
    @Order(2)
    @DisplayName("[PATCH /v3/projects] - Update project")
    fun updateProject() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient(token).patch("/v3/projects/${projects.first().id}") {
            setBody(Project(name = "Renamed project"))
        }

        val newProjects = jsonClient().get("/v3/projects").body<List<Project>>()

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("Project data updated", response.body<Message<String>>().message)
        assertEquals("Renamed project", newProjects.first().name)
    }

    @Test
    @Order(3)
    @DisplayName("[GET /v3/projects/{projectId}] - Get project by id")
    fun getProjectById() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient().get("/v3/projects/${projects.first().id}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Renamed project", response.body<Project>().name)
    }

    @Test
    @Order(4)
    @DisplayName("[DELETE /v3/projects/{projectId}] - Delete project by id")
    fun deleteProjectById() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient(token).delete("/v3/projects/${projects.first().id}")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("Project deleted", response.body<Message<String>>().message)
    }

    @AfterAll
    fun cleanup() = testApplication {
        application(Application::fStatsModule)
        jsonClient(token).delete("/v3/users")
    }
}