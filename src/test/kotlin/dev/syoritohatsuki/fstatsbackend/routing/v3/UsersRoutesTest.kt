package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.Message
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.utils.jsonClient
import dev.syoritohatsuki.fstatsbackend.utils.toUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Users")
@Tags(value = [Tag("v3"), Tag("Users")])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UsersRoutesTest {

    private lateinit var token: String
    private lateinit var userFromToken: User

    @BeforeAll
    fun setup() = testApplication {
        application(Application::fStatsModule)

        jsonClient().post("/v3/auth/registration") {
            setBody(User(username = "ProjectTestUser", password = "Qwerty1234"))
        }

        jsonClient().post("/v3/auth/login") {
            setBody(User(username = "ProjectTestUser", password = "Qwerty1234"))
        }.body<Map<String, String>>().let {
            token = it["token"] ?: throw AssertionError("Can't login to get token for projects tests")
            userFromToken = token.toUser()
        }

        jsonClient(token).post("/v3/projects") {
            setBody(Project(name = "User Project"))
        }

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()
        jsonClient(token).post("/v3/projects/${projects.first().id}/favorite")
    }

    @Test
    @Order(0)
    @DisplayName("[GET /v3/users/{userId}] - Get user by id")
    fun getUserById() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient().get("/v3/users/${userFromToken.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ProjectTestUser", response.body<User>().username)
    }

    @Test
    @Order(1)
    @DisplayName("[GET /v3/users/{userId}/projects] - Get user projects")
    fun getUserProjects() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient().get("/v3/users/${userFromToken.id}/projects")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("User Project", response.body<List<Project>>().first().name)
    }

    @Test
    @Order(2)
    @DisplayName("[DELETE /v3/users] - Delete authorized user]")
    fun deleteUser() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient(token).delete("/v3/users")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("User deleted", response.body<Message<String>>().message)
    }
}