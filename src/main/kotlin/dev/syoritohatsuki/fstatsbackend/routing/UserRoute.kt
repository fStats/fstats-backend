package dev.syoritohatsuki.fstatsbackend.routing

import io.ktor.server.routing.*

fun Route.userRoute() {
    route("users") {
        get("/{id}") {
            //TODO Get user by ID
        }
        post {
            //TODO Create user using body
        }
        delete("/{id}") {
            //TODO Delete by ID
        }
    }
}