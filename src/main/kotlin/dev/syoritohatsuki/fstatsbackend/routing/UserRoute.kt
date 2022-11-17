package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.ExceptionDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute() {
    route("users") {
        get("/{id}") {
            //TODO Get user by ID
        }
        get("/{username}") {
            //TODO Get user by Name
        }
        authenticate("user") {
            delete {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                ProjectDAOImpl.getByOwner(userId).let { projects ->
                    projects.forEach {
                        MetricDAOImpl.removeByProjectId(it.id)
                        ExceptionDAOImpl.removeByProjectId(it.id)
                    }
                }
                UserDAOImpl.deleteById(userId).let {
                    if (it.second == 1) call.respond(HttpStatusCode.OK, "User deleted")
                    else call.respond(HttpStatusCode.NoContent, "User not found")
                }
            }
        }
    }
}