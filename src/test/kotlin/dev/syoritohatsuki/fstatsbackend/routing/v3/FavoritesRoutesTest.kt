package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.Message
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.fStatsModule
import dev.syoritohatsuki.fstatsbackend.utils.SetupTest
import dev.syoritohatsuki.fstatsbackend.utils.jsonClient
import dev.syoritohatsuki.fstatsbackend.utils.toUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@DisplayName("Favorites")
@Tags(value = [Tag("v3"), Tag("Favorites")])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FavoritesRoutesTest : SetupTest() {

    private lateinit var token: String
    private lateinit var userFromToken: User

    @BeforeAll
    fun setup() = testApplication {
        application(Application::fStatsModule)

        jsonClient().post("/v3/auth/registration") {
            setBody(User(username = "FavoriteTest", password = "Qwerty1234"))
        }

        jsonClient().post("/v3/auth/login") {
            setBody(User(username = "FavoriteTest", password = "Qwerty1234"))
        }.body<Map<String, String>>().let {
            token = it["token"] ?: throw AssertionError("Can't login to get token for projects tests")
            userFromToken = token.toUser()
        }

        jsonClient(token).post("/v3/projects") {
            setBody(Project(name = "Favorite Project"))
        }

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()
        jsonClient(token).post("/v3/projects/${projects.first().id}/favorite")
    }

    @Test
    @Order(0)
    @DisplayName("[POST /v3/projects/{projectId}/favorite] - Add project to favorites")
    fun addToFavorite() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient(token).post("/v3/projects/${projects.first().id}/favorite")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("Project added to favorites", response.body<Message<String>>().message)
    }

    @Test
    @Order(1)
    @DisplayName("[GET /v3/users/{userId}/favorite] - List user favorites")
    fun getUserFavorites() = testApplication {
        application(Application::fStatsModule)

        val response = jsonClient(token).get("/v3/users/${userFromToken.id}/favorite")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Favorite Project", response.body<List<Project>>().first().name)
    }

    @Test
    @Order(2)
    @DisplayName("[DELETE - /v3/projects/{projectsId}/favorite] - Remove project from favorites")
    fun removeFromFavorite() = testApplication {
        application(Application::fStatsModule)

        val projects = jsonClient().get("/v3/projects").body<List<Project>>()

        val response = jsonClient(token).delete("/v3/projects/${projects.first().id}/favorite")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals("Project removed from favorites", response.body<Message<String>>().message)
    }

    @AfterAll
    fun cleanup() = testApplication {
        application(Application::fStatsModule)
        jsonClient(token).delete("/v3/users")
    }
}