package dev.syoritohatsuki.fstatsbackend.routing

import dev.syoritohatsuki.fstatsbackend.dao.impl.ExceptionDAOImpl
import dev.syoritohatsuki.fstatsbackend.dao.impl.ProjectDAOImpl
import dev.syoritohatsuki.fstatsbackend.dto.Exception
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exceptionsRoute() {
    route("exceptions") {
        get("{id}") {
            val projectId = call.parameters["id"]

            if (projectId == null || !Regex("^-?\\d+\$").matches(projectId)) {
                call.respond(HttpStatusCode.BadRequest, "Incorrect project ID")
                return@get
            }

            call.respond(ExceptionDAOImpl.getByProject(projectId.toInt()))
        }
        post {
            val exception = call.receive<Exception>()

            if (ProjectDAOImpl.getById(exception.projectId) == null) {
                call.respond(HttpStatusCode.NoContent, "Project not found")
                return@post
            }

            ExceptionDAOImpl.add(exception).let {
                if (it.second == 1) call.respond(HttpStatusCode.Created, "Exception data added")
                else call.respond(HttpStatusCode.OK, "Something went wrong")
            }
        }
    }
}