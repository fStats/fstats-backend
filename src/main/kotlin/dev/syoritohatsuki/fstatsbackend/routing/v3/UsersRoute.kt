package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.PASSWORD_REGEX
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.USERNAME_REGEX
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import dev.syoritohatsuki.fstatsbackend.repository.postgre.PostgresFavoriteRepository
import dev.syoritohatsuki.fstatsbackend.repository.postgre.PostgresProjectRepository
import dev.syoritohatsuki.fstatsbackend.repository.postgre.PostgresUserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoute() {
    route("users") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Project ID must be number"
                )

                val user = PostgresUserRepository.getById(id) ?: return@get call.respondMessage(
                    HttpStatusCode.NoContent, "User not found"
                )

                call.respond(user.getWithoutPassword())
            }
            get("projects") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Project ID must be number"
                )

                val user = PostgresUserRepository.getById(id) ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "User not found"
                )

                call.respond(PostgresProjectRepository.getByOwner(user.id))
            }
            authenticate {
                get("favorite") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                    call.respond(PostgresFavoriteRepository.getUserFavorites(userId))
                }
            }
        }
        authenticate {
            patch {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                val newUserData = call.receive<User>()

                if (newUserData.username.isBlank() && newUserData.password.isBlank()) return@patch call.respondMessage(
                    HttpStatusCode.BadRequest, "No data provided for update"
                )

                if (newUserData.username.isNotBlank() && !Regex(USERNAME_REGEX).matches(newUserData.username)) return@patch call.respondMessage(
                    HttpStatusCode.BadRequest, "Username not match requirements"
                )

                if (newUserData.password.isNotBlank() && !Regex(PASSWORD_REGEX).matches(newUserData.password)) return@patch call.respondMessage(
                    HttpStatusCode.BadRequest, "Password not match requirements"
                )

                when (PostgresUserRepository.updateUserData(userId, newUserData)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "User data updated")
                    else -> call.respondMessage(HttpStatusCode.NoContent, "User not found")
                }
            }

            delete {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                when (PostgresUserRepository.deleteById(userId)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "User deleted")
                    else -> call.respondMessage(HttpStatusCode.NoContent, "User not found")
                }
            }
        }
    }
}