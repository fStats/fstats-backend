package dev.syoritohatsuki.fstatsbackend.routing.v3

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.nycode.bcrypt.hash
import de.nycode.bcrypt.verify
import dev.syoritohatsuki.fstatsbackend.dto.User
import dev.syoritohatsuki.fstatsbackend.mics.*
import dev.syoritohatsuki.fstatsbackend.repository.postgre.PostgresUserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute() {
    route("auth") {
        post("login") {
            val userFromRequest = call.receive<User>()

            if (userFromRequest.username.isEmpty() || userFromRequest.password.isEmpty()) return@post call.respondMessage(
                HttpStatusCode.BadRequest, "Incorrect username or password"
            )

            val userFromDB =
                PostgresUserRepository.getByName(userFromRequest.username) ?: return@post call.respondMessage(
                    HttpStatusCode.BadRequest, "Incorrect username or password"
                )

            if (!verify(
                    userFromRequest.password, userFromDB.passwordHash.toByteArray()
                )
            ) return@post call.respondMessage(
                HttpStatusCode.BadRequest, "Incorrect username or password"
            )

            call.respond(
                mapOf(
                    "token" to JWT.create().withClaim("id", userFromDB.id).withClaim("username", userFromDB.username)
                        .withIssuer("$HOST:$PORT").sign(Algorithm.HMAC256(JWT_SECRET))
                )
            )
        }

        post("registration") {
            val user = call.receive<User>()

            if (!Regex(USERNAME_REGEX).matches(user.username) || !Regex(PASSWORD_REGEX).matches(user.password)) return@post call.respondMessage(
                HttpStatusCode.BadRequest, "Username or password not match requirements"
            )

            when (PostgresUserRepository.create(
                User(username = user.username, passwordHash = String(hash(user.password)))
            )) {
                SUCCESS -> call.respondMessage(HttpStatusCode.Created, "User created")
                else -> call.respondMessage(HttpStatusCode.BadRequest, "Username already exist")
            }
        }
    }
}