package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.ExceptionDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.MetricDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.UserDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Project
import dev.syoritohatsuki.fstatsbackend.mics.getProjectId
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
                        println("${HttpStatusCode.BadRequest} Project not found")
                        return@get call.respond(HttpStatusCode.BadRequest, "Project not found")
                    }

                    call.respond(project)
                }
            }.onFailure {
                UserDAOImpl.getByName(call.parameters["idOrOwner"].toString()).let { user ->
                    if (user == null) {
                        println("${HttpStatusCode.BadRequest} User not found")
                        return@get call.respond(HttpStatusCode.BadRequest, "User not found")
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
                        println("${HttpStatusCode.BadRequest} Project name can't be empty")
                        return@post call.respond(HttpStatusCode.BadRequest, "Project name can't be empty")
                    }

                    ProjectDAOImpl.create(
                        Project(
                            name = project.name,
                            ownerId = call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()
                        )
                    ).let {
                        if (it.second == 1) call.respond(HttpStatusCode.Created) else {
                            call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                            println("${HttpStatusCode.BadRequest} ${it.second}")
                        }
                    }

                }
            }
            delete("{id}") {
                val projectId = call.parameters.getProjectId()

                if (projectId == null) {
                    println("${HttpStatusCode.BadRequest} Incorrect project ID")
                    return@delete call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                }

                ProjectDAOImpl.getById(projectId).let { project ->
                    if (project == null) {
                        println("${HttpStatusCode.NoContent} Project not found")
                        return@delete call.respond(HttpStatusCode.NoContent, "Project not found")
                    }

                    if (project.ownerId == call.principal<JWTPrincipal>()!!.payload.getClaim("id").asInt()) {
                        ProjectDAOImpl.deleteById(project.id).also {
                            if (it.second == 1) {
                                MetricDAOImpl.removeByProjectId(project.id)
                                ExceptionDAOImpl.removeByProjectId(project.id)
                                return@delete call.respond(HttpStatusCode.Accepted, "Project deleted")
                            }
                        }
                    }

                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}