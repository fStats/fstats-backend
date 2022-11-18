package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.ExceptionDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Project
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
        get("{idOrOwner}") {
            kotlin.runCatching {
                ProjectDAOImpl.getById(call.parameters["idOrOwner"]!!.toInt()).let { project ->
                    if (project == null) {
                        call.respond("Project not found")
                        return@get
                    }

                    call.respond(project)
                }
            }.onFailure {
                UserDAOImpl.getByName(call.parameters["idOrOwner"].toString()).let { user ->
                    if (user == null) {
                        call.respond(HttpStatusCode.OK, "User not found")
                        return@get
                    }

                    ProjectDAOImpl.getByOwner(user.id).let { projects ->
                        call.respond(HttpStatusCode.OK, projects)
                    }

                }
            }
        }
        authenticate("user") {
            post {
                call.receive<Project>().let { project ->
                    if (project.name.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, "Project name can't be empty")
                        return@post
                    }

                    ProjectDAOImpl.create(
                        Project(
                            name = project.name,
                            ownerId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                        )
                    ).let {
                        if (it.second == 1) call.respond(HttpStatusCode.Created)
                        else call.respond(HttpStatusCode.BadRequest)
                    }

                }
            }
            delete("{id}") {
                val projectId = call.parameters["id"]

                if (projectId == null || !Regex("(^\\d{1,10}\$)").matches(projectId)) {
                    call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                    return@delete
                }

                ProjectDAOImpl.getById(projectId.toInt()).let { project ->
                    if (project == null) {
                        call.respond(HttpStatusCode.NoContent, "Project not found")
                        return@delete
                    }

                    if (project.ownerId == call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()) {
                        ProjectDAOImpl.deleteById(project.id).also {
                            if (it.second == 1) {
                                MetricDAOImpl.removeByProjectId(project.id)
                                ExceptionDAOImpl.removeByProjectId(project.id)
                                call.respond("Project deleted")
                                return@delete
                            }
                        }
                    }

                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}