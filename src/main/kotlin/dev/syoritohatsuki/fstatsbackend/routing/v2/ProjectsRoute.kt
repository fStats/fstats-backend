package dev.syoritohatsuki.fstatsbackend.routing.v2

import dev.syoritohatsuki.fstatsbackend.dao.impl.FavoriteDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.Database.SUCCESS
import dev.syoritohatsuki.fstatsbackend.mics.respondMessage
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
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondMessage(
                HttpStatusCode.BadRequest, "Project ID must be number"
            )

            val project = ProjectDAOImpl.getById(id) ?: return@get call.respondMessage(
                HttpStatusCode.BadRequest, "Project not found"
            )

            call.respond(project)
        }

        authenticate {
            post {
                val project = call.receive<Project>()

                if (project.name.isBlank()) return@post call.respondMessage(
                    HttpStatusCode.BadRequest, "Project name can't be empty"
                )

                when (ProjectDAOImpl.create(
                    project.name,
                    call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                )) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Created, "Project created")
                    else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect project ID"
                )

                val project = ProjectDAOImpl.getById(id) ?: return@delete call.respondMessage(
                    HttpStatusCode.NoContent, "Project not found"
                )

                if (project.owner.id != (call.principal<JWTPrincipal>()?.payload
                        ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                            ).getClaim("id").asInt()
                ) return@delete call.respond(HttpStatusCode.Unauthorized)

                when (ProjectDAOImpl.deleteById(project.id)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "Project deleted")
                    else -> call.respondMessage(HttpStatusCode.BadRequest, "Something went wrong")
                }
            }

            post("{id}/favorite") {
                val projectId = call.parameters["id"]?.toInt() ?: return@post call.respondMessage(
                    HttpStatusCode.NoContent, "Incorrect project ID"
                )

                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                when (FavoriteDAOImpl.addProjectToFavorites(userId, projectId)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "Project added to favorites")
                    else -> call.respondMessage(HttpStatusCode.NoContent, "Cant add project to favorites")
                }
            }

            delete("{id}/favorite") {

                val projectId = call.parameters["id"]?.toInt() ?: return@delete call.respondMessage(
                    HttpStatusCode.NoContent, "Incorrect project ID"
                )

                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()

                when (FavoriteDAOImpl.removeProjectFromFavorites(userId, projectId)) {
                    SUCCESS -> call.respondMessage(HttpStatusCode.Accepted, "Project removed from favorites")
                    else -> call.respondMessage(HttpStatusCode.NoContent, "Cant remove project from favorites")
                }
            }
        }
    }
}