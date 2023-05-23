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
        // TODO
        get("/{idOrUsername}") {
            kotlin.runCatching {
                UserDAOImpl.getById(call.parameters["idOrUsername"]!!.toInt()).let {
                    if (it == null) {
                        println("${HttpStatusCode.NoContent} User not found")
                        return@get call.respond(HttpStatusCode.NoContent, "User not found")
                    }
                    call.respond(HttpStatusCode.OK, it.getWithoutPassword())
                }
            }.onFailure {
                UserDAOImpl.getByName(call.parameters["idOrUsername"].toString()).let {
                    if (it == null) {
                        println("${HttpStatusCode.NoContent} User not found")
                        return@get call.respond(HttpStatusCode.NoContent, "User not found")
                    }
                    call.respond(HttpStatusCode.OK, it.getWithoutPassword())
                }
            }
        }
        authenticate("user") {
            delete {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                when (UserDAOImpl.deleteById(userId)) {
                    1 -> call.respond(HttpStatusCode.Accepted, "User deleted")
                    else -> call.respond(HttpStatusCode.NoContent, "User not found")
                }
            }
        }
    }
}