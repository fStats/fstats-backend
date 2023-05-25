package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.SUCCESS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.projectsRoute() {
    route("projects") {
        get {
            call.respond(ProjectDAOImpl.getAll())
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Project ID must be number"
            )

            val project = ProjectDAOImpl.getById(id) ?: return@get call.respond(
                HttpStatusCode.BadRequest, "Project not found"
            )

            call.respond(project)
        }

        authenticate {
            post {
                val project = call.receive<Project>()

                if (project.name.isBlank()) return@post call.respond(
                    HttpStatusCode.BadRequest, "Project name can't be empty"
                )

                when (ProjectDAOImpl.create(
                    project.name,
                    call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                )) {
                    SUCCESS -> call.respond(HttpStatusCode.Created)
                    else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val project = ProjectDAOImpl.getById(id) ?: return@delete call.respond(
                    HttpStatusCode.NoContent, "Project not found"
                )

                if (project.ownerId != call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt())
                    return@delete call.respond(HttpStatusCode.Unauthorized)

                when (ProjectDAOImpl.deleteById(project.id)) {
                    SUCCESS -> call.respond(HttpStatusCode.Accepted, "Project deleted")
                    else -> call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                }
            }
        }
    }
}