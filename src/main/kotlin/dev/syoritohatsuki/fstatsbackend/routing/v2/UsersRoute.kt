package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
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
            }.getOrNull() ?: return@get call.respond(HttpStatusCode.NoContent, "User not found")

            call.respond(user.getWithoutPassword())
        }
    }

    authenticate {
        delete {
            val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
            when (UserDAOImpl.deleteById(userId)) {
                1 -> call.respond(HttpStatusCode.Accepted, "User deleted")
                else -> call.respond(HttpStatusCode.NoContent, "User not found")
            }
        }
    }
}