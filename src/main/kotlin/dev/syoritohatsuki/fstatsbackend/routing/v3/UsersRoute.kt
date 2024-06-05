package dev.syoritohatsuki.fstatsbackend.routing.v3

import dev.syoritohatsuki.fstatsbackend.dao.impl.FavoriteDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoute() {
    route("users") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Project ID must be number"
                )

                val user = UserDAOImpl.getById(id) ?: return@get call.respondMessage(
                    HttpStatusCode.NoContent, "User not found"
                )

                call.respond(user.getWithoutPassword())
            }
            get("projects") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "Project ID must be number"
                )

                val user = UserDAOImpl.getById(id) ?: return@get call.respondMessage(
                    HttpStatusCode.BadRequest, "User not found"
                )

                call.respond(ProjectDAOImpl.getByOwner(user.id))
            }
            authenticate {
                get("favorite") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                    call.respond(FavoriteDAOImpl.getUserFavorites(userId))
                }
            }
        }
        authenticate {
            delete {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                when (UserDAOImpl.deleteById(userId)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "User deleted")
                    else -> call.respondMessage(HttpStatusCode.NoContent, "User not found")
                }
            }
        }
    }
}