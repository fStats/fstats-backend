package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.ExceptionDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Exception
import dev.syoritohatsuki.fstatsbackend.mics.getProjectId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exceptionsRoute() {
    route("exceptions") {
        get("{id}") {
            val projectId = call.parameters.getProjectId()

            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                println("${HttpStatusCode.BadRequest} Incorrect project ID")
                return@get
            }

            call.respond(ExceptionDAOImpl.getByProject(projectId))
        }
        post {
            val exception = call.receive<Exception>()

            if (ProjectDAOImpl.getById(exception.projectId) == null) {
                call.respond(HttpStatusCode.NoContent, "Project not found")
                println("${HttpStatusCode.NoContent} Project not found")
                return@post
            }

            ExceptionDAOImpl.add(exception).let {
                if (it.second == 1) call.respond(HttpStatusCode.Created, "Exception data added") else {
                    call.respond(HttpStatusCode.BadRequest, "Something went wrong")
                    println("${HttpStatusCode.BadRequest} ${it.first}")
                }
            }
        }
    }
}