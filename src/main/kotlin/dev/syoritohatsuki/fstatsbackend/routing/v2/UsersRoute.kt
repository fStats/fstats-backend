package dev.syoritohatsuki.fstatsbackend.routing.v2

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
        get("/{idOrUsername}") {
            val idOrUsername = call.parameters["idOrUsername"]

            val user = runCatching {
                UserDAOImpl.getById(idOrUsername!!.toInt())
            }.recover {
                UserDAOImpl.getByName(idOrUsername.toString())
            }.getOrNull() ?: return@get call.respondMessage(HttpStatusCode.NoContent, "User not found")

            call.respond(user.getWithoutPassword())
        }

        get("{id}/projects") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                HttpStatusCode.BadRequest, "Project ID must be number"
            )

            val user = UserDAOImpl.getById(id) ?: return@get call.respondMessage(
                HttpStatusCode.BadRequest, "User not found"
            )

            call.respond(ProjectDAOImpl.getByOwner(user.id))
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